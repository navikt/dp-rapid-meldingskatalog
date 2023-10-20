package no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.db.PostgresDataSourceBuilder.dataSource
import no.nav.dagpenger.rapid.meldingskatalog.helpers.db.Postgres.withMigratedDb
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.LinkedList
import java.util.UUID
import no.nav.dagpenger.rapid.meldingskatalog.melding.System as System1

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

    private fun behov(behovNavn: String) = IdentifisertMelding.Behov(
        navn = behovNavn,
        behandlingskjede = LinkedList<System1>().apply {
            add(System1("tjeneste1"))
            add(System1("tjeneste2"))
            add(System1("tjeneste3"))
        },
        meldingsreferanseId = UUID.randomUUID(),
        opprettet = LocalDateTime.now().minusMinutes(1),
        mottatt = LocalDateTime.now(),
    )
}
