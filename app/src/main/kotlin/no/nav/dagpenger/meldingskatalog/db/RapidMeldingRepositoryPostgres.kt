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
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Sporing
import no.nav.dagpenger.meldingskatalog.melding.Pakkeinnhold
import no.nav.dagpenger.meldingskatalog.melding.RapidMelding
import org.postgresql.util.PGobject
import java.util.UUID

class RapidMeldingRepositoryPostgres : RapidMeldingRepository {
    private val observers = mutableSetOf<RapidMeldingRepositoryObserver>()

    override fun <T : Innholdstype> lagreMelding(melding: RapidMelding<T>) =
        sessionOf(dataSource).use {
            it.transaction { tx ->
                tx.lagreMelding(melding)
                tx.lagreSporing(melding.meldingsreferanseId, melding.sporing)
                tx.lagreInnhold(melding.meldingsreferanseId, melding.innhold)
            }
        }.also {
            observers.forEach { observer -> observer.onMessageAdded(melding) }
        }

    private fun <T : Innholdstype> Session.lagreMelding(melding: RapidMelding<T>) {
        run(
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
    }

    private fun Session.lagreSporing(
        meldingsreferanseId: UUID,
        sporing: List<Sporing>,
    ) = sporing.forEach { spor ->
        this.run(lagreSporing(meldingsreferanseId, spor))
    }

    private fun lagreSporing(
        meldingsreferanseId: UUID,
        sporing: Sporing,
    ) = queryOf(
        //language=PostgreSQL
        """
        INSERT INTO sporing (sporing_id, time, service, instance, image, meldingsreferanse_id)
        VALUES (:sporing_id, :time, :service, :instance, :image, :meldingsreferanseId)
        """.trimIndent(),
        mapOf(
            "sporing_id" to sporing.id,
            "time" to sporing.time,
            "service" to sporing.service,
            "instance" to sporing.instance,
            "image" to sporing.image,
            "meldingsreferanseId" to meldingsreferanseId,
        ),
    ).asUpdate

    private fun <T> Session.lagreInnhold(
        meldingsreferanseId: UUID,
        pakkeinnhold: List<T>,
    ) {
        pakkeinnhold.map { innhold ->
            when (innhold) {
                is HendelseType -> lagreInnholdHendelse(meldingsreferanseId, innhold)
                is BehovType -> lagreInnholdBehov(meldingsreferanseId, innhold)
                is LøsningType -> lagreInnholdLøsning(meldingsreferanseId, innhold)
                else -> throw IllegalArgumentException("Ukjent innholdstype")
            }
        }.forEach { this.run(it) }
    }

    private fun lagreInnholdHendelse(
        meldingsreferanseId: UUID,
        innhold: HendelseType,
    ) = queryOf(
        //language=PostgreSQL
        """
        INSERT INTO melding_innhold_hendelse (meldingsreferanse_id, navn)
        VALUES (:meldingsreferanseId, :navn)
        """.trimIndent(),
        mapOf(
            "meldingsreferanseId" to meldingsreferanseId,
            "navn" to innhold.navn,
        ),
    ).asUpdate

    private fun lagreInnholdBehov(
        meldingsreferanseId: UUID,
        innhold: BehovType,
    ) = queryOf(
        //language=PostgreSQL
        """
        INSERT INTO melding_innhold_behov (meldingsreferanse_id, behov_id, behov)
        VALUES (:meldingsreferanseId, :behovId, :behov)
        """.trimIndent(),
        mapOf(
            "meldingsreferanseId" to meldingsreferanseId,
            "behovId" to innhold.behovId,
            "behov" to innhold.behov,
        ),
    ).asUpdate

    private fun lagreInnholdLøsning(
        meldingsreferanseId: UUID,
        innhold: LøsningType,
    ) = queryOf(
        //language=PostgreSQL
        """
        INSERT INTO melding_innhold_løsning (meldingsreferanse_id, behov_id, løsning)
        VALUES (:meldingsreferanseId, :behovId, :losning)
        """.trimIndent(),
        mapOf(
            "meldingsreferanseId" to meldingsreferanseId,
            "behovId" to innhold.behovId,
            "losning" to innhold.løser,
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
                        konvolutt = session.konvolutt(row),
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
                        konvolutt = session.konvolutt(row),
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

    private fun Session.konvolutt(row: Row): Konvolutt {
        val meldingsreferanseId = row.uuid("meldingsreferanse_id")
        return Konvolutt(
            meldingsreferanseId = meldingsreferanseId,
            opprettet = row.localDateTime("opprettet"),
            eventName = row.string("event_name"),
            sporing = hentSporing(meldingsreferanseId),
        )
    }

    private fun Session.hentSporing(meldingsreferanseId: UUID) =
        this.run(
            queryOf(
                //language=PostgreSQL
                """
                SELECT * FROM sporing WHERE meldingsreferanse_id = ? ORDER BY id 
                """.trimIndent(),
                meldingsreferanseId,
            ).map { row ->
                Sporing(
                    id = row.uuid("sporing_id"),
                    time = row.localDateTime("time"),
                    service = row.string("service"),
                    instance = row.string("instance"),
                    image = row.string("image"),
                )
            }.asList,
        )

    override fun leggTilObserver(observer: RapidMeldingRepositoryObserver) = observers.add(observer)

    override fun hentMeldingstyper(): List<Meldingstype> {
        return sessionOf(dataSource).use {
            it.run(
                queryOf(
                    //language=PostgreSQL
                    """
                    SELECT navn, behov, COUNT(DISTINCT m.meldingsreferanse_id) AS antall 
                    FROM melding m
                    LEFT JOIN melding_innhold_hendelse mih ON m.meldingsreferanse_id = mih.meldingsreferanse_id
                    LEFT JOIN melding_innhold_behov mib ON m.meldingsreferanse_id = mib.meldingsreferanse_id
                    WHERE (mih.navn IS NOT NULL OR mib.behov IS NOT NULL)
                    GROUP BY navn, behov
                    ORDER BY antall DESC, navn
                    """.trimIndent(),
                ).map { row ->
                    val hendelse = row.stringOrNull("navn")
                    val behov = row.stringOrNull("behov")
                    val type =
                        when (behov == null) {
                            true -> "behov"
                            false -> "hendelse"
                        }
                    val navn = hendelse ?: behov ?: throw IllegalArgumentException("Ukjent meldingstype")
                    Meldingstype(
                        navn = navn,
                        type = type,
                        antall = row.int("antall"),
                        involverteTjenester = emptyList(),
                    )
                }.asList,
            )
        }
    }
}
