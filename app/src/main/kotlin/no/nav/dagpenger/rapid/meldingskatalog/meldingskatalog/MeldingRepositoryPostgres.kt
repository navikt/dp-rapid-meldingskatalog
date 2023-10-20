package no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog

import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf
import kotliquery.using
import no.nav.dagpenger.rapid.meldingskatalog.melding.Behandlingskjede
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding
import java.time.LocalDateTime
import java.util.UUID
import javax.sql.DataSource

class MeldingRepositoryPostgres(private val ds: DataSource) : MeldingRepository {
    override fun lagre(melding: IdentifisertMelding) {
        using(sessionOf(ds)) { session ->
            session.transaction { tx ->
                tx.lagreMeldingstype(melding.navn, melding.type)
                tx.tellMelding(melding.meldingsreferanseId, melding.navn, melding.opprettet, melding.mottatt)
                tx.lagreBehandlingskjede(melding.meldingsreferanseId, melding.behandlingskjede)
            }
        }
    }

    private fun Session.lagreMeldingstype(navn: String, type: String) {
        run(
            queryOf(
                //language=PostgreSQL
                "INSERT INTO meldingstyper (navn, type) VALUES (:navn,:type) ON CONFLICT DO NOTHING",
                mapOf(
                    "navn" to navn,
                    "type" to type,
                ),
            ).asExecute,
        )
    }

    private fun Session.tellMelding(uuid: UUID, navn: String, opprettet: LocalDateTime, mottatt: LocalDateTime) {
        run(
            queryOf(
                //language=PostgreSQL
                """
                INSERT INTO meldingslogg (meldingsreferanse_id, meldingstype_id, opprettet, mottatt)
                VALUES (:uuid, (SELECT id FROM meldingstyper WHERE navn = :navn), :opprettet, :mottatt)
                ON CONFLICT DO NOTHING
                """.trimIndent(),
                mapOf(
                    "uuid" to uuid,
                    "navn" to navn,
                    "opprettet" to opprettet,
                    "mottatt" to mottatt,
                ),
            ).asExecute,
        )
    }

    private fun Session.lagreBehandlingskjede(meldingsreferanseId: UUID, behandlingskjede: Behandlingskjede) {
        val systemer = behandlingskjede.map { it.navn }
        systemer.forEach { system ->
            run(
                queryOf(
                    //language=PostgreSQL
                    """
                    INSERT INTO systemer (navn)
                    VALUES (:navn)
                    ON CONFLICT DO NOTHING
                    """.trimIndent(),
                    mapOf(
                        "navn" to system,
                    ),
                ).asExecute,
            )
        }
        behandlingskjede.forEachIndexed { index, system ->
            run(
                queryOf(
                    //language=PostgreSQL
                    """
                    INSERT INTO behandlingskjeder (meldingslogg_id, system_id, meldingstype_id, indeks)
                    VALUES ((SELECT meldingsreferanse_id FROM meldingslogg WHERE meldingsreferanse_id = :meldingsreferanseId),
                            (SELECT id FROM systemer WHERE navn = :navn),
                            (SELECT meldingstype_id FROM meldingslogg WHERE meldingsreferanse_id = :meldingsreferanseId),
                            :indeks)
                    ON CONFLICT DO NOTHING
                    """.trimIndent(),
                    mapOf(
                        "meldingsreferanseId" to meldingsreferanseId,
                        "navn" to system.navn,
                        "indeks" to index,
                    ),
                ).asExecute,
            )
        }
    }
}
