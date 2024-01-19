package no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog

import no.nav.dagpenger.meldingskatalog.Behandlingskjede
import no.nav.dagpenger.meldingskatalog.Meldingsinformasjon
import no.nav.dagpenger.meldingskatalog.Tjeneste
import no.nav.dagpenger.rapid.meldingskatalog.db.PostgresDataSourceBuilder.dataSource
import no.nav.dagpenger.rapid.meldingskatalog.helpers.db.Postgres.withMigratedDb
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class MeldingRepositoryPostgresTest {
    @Test
    fun `kan lagre en melding og behandlingskjede`() {
        withMigratedDb {
            val meldingRepository = MeldingRepositoryPostgres(dataSource)

            meldingRepository.lagre(behov("foo"))
            meldingRepository.lagre(behov("bar"))
            meldingRepository.lagre(behov("foobar"))
        }
    }

    private fun behov(behovNavn: String) =
        Meldingsinformasjon(
            navn = behovNavn,
            type = "behov",
            behandlingskjede =
                Behandlingskjede().apply {
                    add(Tjeneste("tjeneste1"))
                    add(Tjeneste("tjeneste2"))
                    add(Tjeneste("tjeneste3"))
                },
            meldingsreferanseId = UUID.randomUUID(),
            opprettet = LocalDateTime.now().minusMinutes(1),
            mottatt = LocalDateTime.now(),
        )
}
