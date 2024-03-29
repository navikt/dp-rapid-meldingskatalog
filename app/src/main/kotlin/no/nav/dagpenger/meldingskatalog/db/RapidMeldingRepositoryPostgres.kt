package no.nav.dagpenger.meldingskatalog.db

import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.dagpenger.meldingskatalog.db.PostgresDataSourceBuilder.dataSource
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.BehovType
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.HendelseType
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.LøsningType
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.dagpenger.meldingskatalog.melding.Pakkeinnhold
import no.nav.dagpenger.meldingskatalog.melding.RapidMelding
import org.postgresql.util.PGobject
import java.util.UUID

class RapidMeldingRepositoryPostgres : RapidMeldingRepository {
    private val observers = mutableSetOf<RapidMeldingRepositoryObserver>()

    override fun <T : Innholdstype> lagreMelding(melding: RapidMelding<T>) =
        sessionOf(dataSource).use {
            it.transaction { tx ->
                tx.run(
                    queryOf(
                        //language=PostgreSQL
                        """
                        INSERT INTO melding (meldingsreferanse_id, opprettet, event_name, data)
                        VALUES (:meldingsreferanseId, :opprettet, :eventName, :data)
                        ON CONFLICT DO NOTHING
                        """.trimIndent(),
                        mapOf(
                            "meldingsreferanseId" to melding.meldingsreferanseId,
                            "opprettet" to melding.opprettet,
                            "eventName" to melding.eventName,
                            "data" to
                                PGobject().apply {
                                    type = "json"
                                    value = melding.json
                                },
                        ),
                    ).asUpdate,
                )
                melding.innhold.map { innhold: T ->
                    when (innhold) {
                        is HendelseType -> lagreInnholdHendelse(melding, innhold)
                        is BehovType -> lagreInnholdBehov(melding, innhold)
                        is LøsningType -> lagreInnholdLøsning(melding, innhold)
                        else -> throw IllegalArgumentException("Ukjent innholdstype")
                    }
                }.forEach { query -> tx.run(query) }
            }
        }.also {
            observers.forEach { observer -> observer.onMessageAdded(melding) }
        }

    private fun <T : Innholdstype> lagreInnholdHendelse(
        melding: RapidMelding<T>,
        innhold: HendelseType,
    ) = queryOf(
        //language=PostgreSQL
        """
        INSERT INTO melding_innhold_hendelse (meldingsreferanse_id, navn)
        VALUES (:meldingsreferanseId, :navn)
        """.trimIndent(),
        mapOf(
            "meldingsreferanseId" to melding.meldingsreferanseId,
            "navn" to innhold.toString(),
        ),
    ).asUpdate

    private fun <T : Innholdstype> lagreInnholdBehov(
        melding: RapidMelding<T>,
        innhold: BehovType,
    ) = queryOf(
        //language=PostgreSQL
        """
        INSERT INTO melding_innhold_behov (meldingsreferanse_id, behov_id, behov)
        VALUES (:meldingsreferanseId, :behovId, :behov)
        """.trimIndent(),
        mapOf(
            "meldingsreferanseId" to melding.meldingsreferanseId,
            "behovId" to innhold.behovId,
            "behov" to innhold.behov,
        ),
    ).asUpdate

    private fun <T : Innholdstype> lagreInnholdLøsning(
        melding: RapidMelding<T>,
        innhold: LøsningType,
    ) = queryOf(
        //language=PostgreSQL
        """
        INSERT INTO melding_innhold_løsning (meldingsreferanse_id, behov_id, løsning)
        VALUES (:meldingsreferanseId, :behovId, :løsning)
        """.trimIndent(),
        mapOf(
            "meldingsreferanseId" to melding.meldingsreferanseId,
            "behovId" to innhold.behovId,
            "løsning" to innhold.løser,
        ),
    ).asUpdate

    override fun hentMeldinger() =
        sessionOf(dataSource).use { session ->
            session.run(
                queryOf(
                    //language=PostgreSQL
                    """
                    SELECT * FROM melding ORDER BY opprettet DESC LIMIT 100
                    """.trimIndent(),
                ).map { row ->
                    val innhold = session.hentInnhold(row.uuid("meldingsreferanse_id"))
                    RapidMelding(
                        konvolutt = row.konvolutt(),
                        innhold = Pakkeinnhold(innhold),
                        json = row.string("data"),
                    )
                }.asList,
            )
        }

    override fun hentMelding(meldingsreferanseId: UUID) =
        sessionOf(dataSource).use { session ->
            session.run(
                queryOf(
                    //language=PostgreSQL
                    """
                    SELECT * FROM melding
                    WHERE meldingsreferanse_id = ?
                    """.trimIndent(),
                    meldingsreferanseId,
                ).map { row ->
                    val innhold = session.hentInnhold(meldingsreferanseId)
                    RapidMelding(
                        konvolutt = row.konvolutt(),
                        innhold = Pakkeinnhold(innhold),
                        json = row.string("data"),
                    )
                }.asSingle,
            )!!
        }

    private fun Session.hentInnhold(meldingsreferanseId: UUID) =
        hentInnholdHendelse(meldingsreferanseId) +
            hentInnholdBehov(meldingsreferanseId) +
            hentInnholdLøsning(meldingsreferanseId)

    private fun Session.hentInnholdHendelse(meldingsreferanseId: UUID) =
        this.run(
            queryOf(
                //language=postgresql
                "SELECT * FROM melding_innhold_hendelse WHERE meldingsreferanse_id = ?",
                meldingsreferanseId,
            ).map {
                HendelseType(it.string("navn"))
            }.asList,
        )

    private fun Session.hentInnholdBehov(meldingsreferanseId: UUID) =
        this.run(
            queryOf(
                //language=postgresql
                "SELECT * FROM melding_innhold_behov WHERE meldingsreferanse_id = ?",
                meldingsreferanseId,
            ).map {
                BehovType(it.uuid("behov_id"), it.string("behov"))
            }.asList,
        )

    private fun Session.hentInnholdLøsning(meldingsreferanseId: UUID) =
        this.run(
            queryOf(
                //language=postgresql
                "SELECT * FROM melding_innhold_løsning WHERE meldingsreferanse_id = ?",
                meldingsreferanseId,
            ).map {
                LøsningType(it.uuid("behov_id"), it.string("løsning"))
            }.asList,
        )

    private fun Row.konvolutt() =
        Konvolutt(
            meldingsreferanseId = uuid("meldingsreferanse_id"),
            opprettet = localDateTime("opprettet"),
            eventName = string("event_name"),
            sporing = emptyList(),
        )

    override fun leggTilObserver(observer: RapidMeldingRepositoryObserver) = observers.add(observer)
}
