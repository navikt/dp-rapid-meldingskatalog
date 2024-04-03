package no.nav.dagpenger.meldingskatalog.behov

import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.dagpenger.meldingskatalog.db.PostgresDataSourceBuilder.dataSource
import java.time.LocalDateTime
import java.util.UUID

class BehovRepositoryPostgres : BehovRepository {
    override fun hentBehov(behovId: UUID) =
        sessionOf(dataSource).use { session ->
            val behovSet = mutableSetOf<String>()
            val løsningSet = mutableSetOf<String>()
            val meldingerSet = mutableSetOf<UUID>()
            session.run(
                queryOf(
                    //language=PostgreSQL
                    """
                    SELECT b.behov_id,
                           b.opprettet,
                           b.ferdig,
                           bb.behov   AS behov_item,
                           bl.løsning AS løsning_item,
                           bm.melding AS melding_item
                    FROM behov b
                             LEFT JOIN behov_behov bb ON b.behov_id = bb.behov_id
                             LEFT JOIN behov_løsning bl ON b.behov_id = bl.behov_id
                             LEFT JOIN behov_meldinger bm ON b.behov_id = bm.behov_id
                    WHERE b.behov_id = :behovId
                    """.trimIndent(),
                    mapOf("behovId" to behovId),
                ).map { row ->
                    row.stringOrNull("behov_item")?.let { behovSet.add(it) }
                    row.stringOrNull("løsning_item")?.let { løsningSet.add(it) }
                    row.uuidOrNull("melding_item")?.let { meldingerSet.add(it) }
                    BehovDTO(
                        opprettet = row.localDateTime("opprettet"),
                        ferdig = row.localDateTimeOrNull("ferdig"),
                    )
                }.asSingle,
            )?.let { dto ->
                Behov(
                    behovId = behovId,
                    opprettet = dto.opprettet,
                    ferdig = dto.ferdig,
                    behov = behovSet,
                    løsning = løsningSet,
                    meldinger = meldingerSet,
                )
            }
        } ?: Behov(behovId)

    private data class BehovDTO(val opprettet: LocalDateTime, val ferdig: LocalDateTime?)

    override fun hentBehov(): List<Behov> {
        val uuids =
            sessionOf(dataSource).use { session ->
                session.run(
                    queryOf(
                        //language=PostgreSQL
                        "SELECT behov_id FROM behov ORDER BY opprettet DESC LIMIT 10",
                    ).map { it.uuid("behov_id") }.asList,
                )
            }

        return uuids.map { hentBehov(it) }
    }

    override fun lagreBehov(behov: Behov) =
        sessionOf(dataSource).use { session ->
            session.transaction { tx ->
                tx.run(
                    queryOf(
                        //language=PostgreSQL
                        """
                        INSERT INTO behov (behov_id, opprettet, ferdig)
                        VALUES (:behovId, :opprettet, :ferdig)
                        ON CONFLICT (behov_id) DO UPDATE SET ferdig = :ferdig
                        """.trimIndent(),
                        mapOf(
                            "behovId" to behov.behovId,
                            "opprettet" to behov.opprettet,
                            "ferdig" to behov.ferdig,
                        ),
                    ).asUpdate,
                )
                behov.behov.map {
                    queryOf(
                        //language=PostgreSQL
                        """
                        INSERT INTO behov_behov (behov_id, behov)
                        VALUES (:behovId, :behov)
                        """.trimIndent(),
                        mapOf(
                            "behovId" to behov.behovId,
                            "behov" to it,
                        ),
                    ).asUpdate
                }.forEach { tx.run(it) }
                behov.løsning.map {
                    queryOf(
                        //language=PostgreSQL
                        """
                        INSERT INTO behov_løsning (behov_id, løsning)
                        VALUES (:behovId, :losning)
                        """.trimIndent(),
                        mapOf(
                            "behovId" to behov.behovId,
                            "losning" to it,
                        ),
                    ).asUpdate
                }.forEach { tx.run(it) }
                behov.meldinger.map {
                    queryOf(
                        //language=PostgreSQL
                        """
                        INSERT INTO behov_meldinger (behov_id, melding)
                        VALUES (:behovId, :melding)
                        """.trimIndent(),
                        mapOf(
                            "behovId" to behov.behovId,
                            "melding" to it,
                        ),
                    ).asUpdate
                }.forEach { tx.run(it) }
            }
        }
}
