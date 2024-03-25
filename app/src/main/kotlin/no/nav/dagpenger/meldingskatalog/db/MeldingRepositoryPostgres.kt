package no.nav.dagpenger.meldingskatalog.db

import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.dagpenger.meldingskatalog.db.PostgresDataSourceBuilder.dataSource
import no.nav.dagpenger.meldingskatalog.melding.Behov
import no.nav.dagpenger.meldingskatalog.melding.Hendelse
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.dagpenger.meldingskatalog.melding.Løsning
import no.nav.dagpenger.meldingskatalog.melding.Melding
import no.nav.dagpenger.meldingskatalog.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.meldingskatalog.systemkatalog.System
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.toUUID
import org.postgresql.util.PGobject
import java.util.UUID

class MeldingRepositoryPostgres : MeldingRepository {
    private val observers = mutableListOf<MeldingRepositoryObserver>()

    private val meldinger = mutableListOf<Melding>()
    private val messages = mutableMapOf<String, String>()

    override fun lagre(
        message: JsonMessage,
        melding: Melding,
    ) {
        sessionOf(dataSource).use {
            it.transaction { tx ->
                tx.lagreMessage(message)
                tx.lagreMelding(melding)
            }
        }
        observers.forEach { it.nyMelding(melding) }
    }

    private fun Session.lagreMessage(message: JsonMessage) =
        this.run(
            queryOf(
                //language=PostgreSQL
                """
                INSERT INTO message 
                    (message_id, data, lest_dato)
                VALUES
                    (:melding_id, :data, NOW())
                ON CONFLICT DO NOTHING
                """.trimIndent(),
                mapOf(
                    "melding_id" to message.id.toUUID(),
                    "data" to
                        PGobject().apply {
                            type = "json"
                            value = message.toJson()
                        },
                ),
            ).asUpdate,
        )

    private fun Session.lagreMelding(melding: Melding) {
        this.run(
            queryOf(
                //language=PostgreSQL
                """
                INSERT INTO melding 
                    (meldingsreferanse_id, type, opprettet, event_name)
                VALUES
                    (:meldingsreferanseId, :type, :opprettet, :eventName)
                ON CONFLICT DO NOTHING
                """.trimIndent(),
                mapOf(
                    "meldingsreferanseId" to melding.meldingsreferanseId,
                    "type" to melding.javaClass.simpleName,
                    "opprettet" to melding.opprettet,
                    "eventName" to melding.eventName,
                ),
            ).asUpdate,
        )
        when (melding) {
            is Hendelse -> {}
            is Behov -> this.lagreBehov(melding)
            is Løsning -> this.lagreLøser(melding)
        }
    }

    private fun Session.lagreBehov(melding: Behov) {
        this.run(
            queryOf(
                //language=PostgreSQL
                """
                INSERT INTO melding_behov (meldingsreferanse_id, behov_id)
                VALUES (:meldingsreferanseId, :behovId)
                ON CONFLICT DO NOTHING
                """.trimIndent(),
                mapOf(
                    "meldingsreferanseId" to melding.meldingsreferanseId,
                    "behovId" to melding.behovId,
                ),
            ).asUpdate,
        )
        this.batchPreparedNamedStatement(
            //language=PostgreSQL
            """
            INSERT INTO behov_behov (meldingsreferanse_id, behov)
            VALUES (:meldingsreferanseId, :behov)
            """.trimIndent(),
            melding.behov.map { mapOf("meldingsreferanseId" to melding.meldingsreferanseId, "behov" to it) },
        )
    }

    private fun Session.lagreLøser(melding: Løsning) {
        this.run(
            queryOf(
                //language=PostgreSQL
                """
                INSERT INTO melding_behov (meldingsreferanse_id, behov_id)
                VALUES (:meldingsreferanseId, :behovId)
                ON CONFLICT DO NOTHING
                """.trimIndent(),
                mapOf(
                    "meldingsreferanseId" to melding.meldingsreferanseId,
                    "behovId" to melding.behovId,
                ),
            ).asUpdate,
        )
        this.batchPreparedNamedStatement(
            //language=PostgreSQL
            """
            INSERT INTO behov_løser (meldingsreferanse_id, løser)
            VALUES (:meldingsreferanseId, :loser)
            """.trimIndent(),
            melding.løser.map { mapOf("meldingsreferanseId" to melding.meldingsreferanseId, "loser" to it) },
        )
    }

    override fun hentMeldinger() =
        sessionOf(dataSource).use { session ->
            session.run(
                queryOf(
                    //language=PostgreSQL
                    """
                    SELECT meldingsreferanse_id, type, opprettet, event_name FROM melding
                    """.trimIndent(),
                ).map { row ->
                    val meldingsreferanseId = row.uuid("meldingsreferanse_id")
                    val konvolutt =
                        Konvolutt(
                            meldingsreferanseId = meldingsreferanseId,
                            opprettet = row.localDateTime("opprettet"),
                            eventName = row.string("event_name"),
                            sporing = emptyList(),
                        )

                    when (val type = row.string("type")) {
                        Hendelse::class.java.simpleName -> Hendelse(konvolutt)
                        Behov::class.java.simpleName -> {
                            val (behovId, behov) = session.hentBehov(meldingsreferanseId)
                            Behov(behovId, behov, konvolutt)
                        }

                        Løsning::class.java.simpleName -> {
                            val (behovId, løser) = session.hentLøsning(meldingsreferanseId)
                            Løsning(behovId, løser, konvolutt)
                        }

                        else -> throw IllegalArgumentException("Ukjent meldingstype=$type")
                    }
                }.asList,
            )
        }

    private fun Session.hentBehov(meldingsreferanseId: UUID) =
        this.run(
            queryOf(
                //language=PostgreSQL
                """
                SELECT melding_behov.behov_id, ARRAY_AGG(behov) AS behov
                FROM melding_behov 
                    LEFT JOIN behov_behov bb ON melding_behov.meldingsreferanse_id = bb.meldingsreferanse_id
                WHERE melding_behov.meldingsreferanse_id = :meldingsreferanse_id
                GROUP BY melding_behov.behov_id
                """.trimIndent(),
                mapOf("meldingsreferanse_id" to meldingsreferanseId),
            ).map { row ->
                row.uuid("behov_id") to row.array<String>("behov").toList()
            }.asSingle,
        )!!

    private fun Session.hentLøsning(meldingsreferanseId: UUID) =
        this.run(
            queryOf(
                //language=PostgreSQL
                """
                SELECT melding_behov.behov_id, ARRAY_AGG(løser) AS løser
                FROM melding_behov 
                    LEFT JOIN behov_løser bl ON melding_behov.meldingsreferanse_id = bl.meldingsreferanse_id
                WHERE melding_behov.meldingsreferanse_id = :meldingsreferanse_id
                GROUP BY melding_behov.behov_id
                """.trimIndent(),
                mapOf("meldingsreferanse_id" to meldingsreferanseId),
            ).map { row ->
                row.uuid("behov_id") to row.array<String>("løser").toList()
            }.asSingle,
        )!!

    override fun hentMeldingstyper(): List<Meldingskatalog> {
        TODO("Not yet implemented")
    }

    override fun hentSystem(): List<System> {
        TODO("Not yet implemented")
    }

    override fun hentMessage(meldingsreferanseId: String) =
        sessionOf(dataSource).use {
            it.run(
                queryOf(
                    //language=PostgreSQL
                    """
                    SELECT data FROM message WHERE message_id = :melding_id
                    """.trimIndent(),
                    mapOf("melding_id" to meldingsreferanseId),
                ).map { row ->
                    row.string("data")
                }.asSingle,
            ) ?: throw IllegalArgumentException("Melding med meldingreferanseId=$meldingsreferanseId finnes ikke")
        }
}
