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
    override fun hentMeldingstyper() = using(sessionOf(ds)) { session ->
        session.run(
            queryOf(
                //language=PostgreSQL
                """
                SELECT
                    mt.navn AS message_name,
                    mt.type AS message_type,
                    COUNT(ml.meldingstype_id) AS instance_count,
                    ARRAY_AGG(DISTINCT s.navn) AS connected_services
                FROM meldingstyper mt
                LEFT JOIN meldingslogg ml ON mt.id = ml.meldingstype_id
                LEFT JOIN behandlingskjeder bk ON ml.meldingsreferanse_id = bk.meldingslogg_id
                LEFT JOIN systemer s ON bk.system_id = s.id
                GROUP BY mt.navn, mt.type;
                """.trimIndent(),
            ).map { row ->
                Meldingstype(
                    navn = row.string("message_name"),
                    type = row.string("message_type"),
                    antall = row.int("instance_count"),
                    system = row.array<String>("connected_services").toList().filterNot { it.isBlank() },
                )
            }.asList,
        )
    }

    override fun hentMeldingstype(meldingstype: String) = using(sessionOf(ds)) { session ->
        session.run(
            queryOf(
                //language=PostgreSQL
                """
                SELECT
                    mt.navn AS message_name,
                    mt.type AS message_type,
                    COUNT(ml.meldingstype_id) AS instance_count,
                    ARRAY_AGG(DISTINCT s.navn) AS connected_services
                FROM meldingstyper mt
                LEFT JOIN meldingslogg ml ON mt.id = ml.meldingstype_id
                LEFT JOIN behandlingskjeder bk ON ml.meldingsreferanse_id = bk.meldingslogg_id
                LEFT JOIN systemer s ON bk.system_id = s.id
                WHERE mt.navn = :meldingstype
                GROUP BY mt.navn, mt.type;
                """.trimIndent(),
                mapOf("meldingstype" to meldingstype),
            ).map { row ->
                Meldingstype(
                    navn = row.string("navn"),
                    type = row.string("type"),
                    antall = row.int("antall"),
                    system = row.array<String>("connected_services").toList().filterNot { it.isBlank() },
                )
            }.asSingle,
        )
    } ?: throw IllegalArgumentException("Meldingstype $meldingstype finnes ikke")

    override fun hentMeldinger(meldingstype: String) = using(sessionOf(ds)) { session ->
        session.run(
            queryOf(
                //language=PostgreSQL
                """
                SELECT ml.meldingsreferanse_id AS message_reference_id,
                       mt.navn                 AS message_name,
                       mt.type                 AS message_type,
                       ml.opprettet            AS created_at,
                       ml.mottatt              AS received_at
                FROM meldingslogg ml
                JOIN meldingstyper mt ON ml.meldingstype_id = mt.id
                WHERE mt.type = :meldingstype
                ORDER BY ml.opprettet DESC;
                """.trimIndent(),
                mapOf("meldingstype" to meldingstype),
            ).map { row ->
                Melding(
                    meldingsreferanseId = row.uuid("message_reference_id"),
                    navn = row.string("message_name"),
                    type = row.string("message_type"),
                    opprettet = row.localDateTime("created_at"),
                    mottatt = row.localDateTime("received_at"),
                )
            }.asList,
        )
    }

    override fun hentSystem() = using(sessionOf(ds)) { session ->
        session.run(
            queryOf(
                //language=PostgreSQL
                """
                SELECT
                    s.navn AS system_name,
                    mt.navn AS message_name,
                    mt.type AS message_type,
                    COUNT(*) AS message_type_count
                FROM systemer s
                JOIN behandlingskjeder bk ON s.id = bk.system_id
                JOIN meldingstyper mt ON bk.meldingstype_id = mt.id
                GROUP BY s.navn, mt.navn, mt.type
                ORDER BY s.navn, mt.navn;
                """.trimIndent(),
            ).map { row ->
                System(
                    navn = row.string("system_name"),
                    meldingstyper = listOf(
                        Meldingstype(
                            navn = row.string("message_name"),
                            type = row.string("message_type"),
                            antall = 0,
                            system = emptyList(),
                        ),
                    ),
                )
            }.asList,
        )
    }

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
