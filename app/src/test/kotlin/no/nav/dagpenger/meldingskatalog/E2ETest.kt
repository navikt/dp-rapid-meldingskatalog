package no.nav.dagpenger.meldingskatalog

import io.kotest.matchers.collections.shouldHaveSize
import no.nav.dagpenger.meldingskatalog.db.MeldingRepositoryPostgres
import no.nav.dagpenger.meldingskatalog.db.Postgres.withMigratedDb
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.behov
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.hendelse
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.løsning
import no.nav.dagpenger.meldingskatalog.rivers.BehovRiver
import no.nav.dagpenger.meldingskatalog.rivers.HendelseRiver
import no.nav.dagpenger.meldingskatalog.rivers.LøsningRiver
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test

class E2ETest {
    private val rapid = TestRapid()
    private val repository = MeldingRepositoryPostgres()

    init {
        HendelseRiver(rapid, repository)
        BehovRiver(rapid, repository)
        LøsningRiver(rapid, repository)
    }

    @Test
    fun `tar imot meldinger og lager kataloger over system og meldingstyper`() {
        withMigratedDb {
            rapid.sendTestMessage(hendelse.toJson())
            rapid.sendTestMessage(behov.toJson())
            rapid.sendTestMessage(løsning.toJson())

            repository.hentMeldinger() shouldHaveSize 3
        }
    }
}
