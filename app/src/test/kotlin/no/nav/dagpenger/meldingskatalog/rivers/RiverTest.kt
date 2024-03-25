package no.nav.dagpenger.meldingskatalog.rivers

import io.kotest.matchers.collections.shouldHaveSingleElement
import no.nav.dagpenger.meldingskatalog.db.MeldingRepositoryInMemory
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.behov
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.hendelse
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.løsning
import no.nav.dagpenger.meldingskatalog.melding.Behov
import no.nav.dagpenger.meldingskatalog.melding.Hendelse
import no.nav.dagpenger.meldingskatalog.melding.Løsning
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test

class RiverTest {
    private val rapid = TestRapid()
    private val repository = MeldingRepositoryInMemory()

    init {
        HendelseRiver(rapid, repository)
        BehovRiver(rapid, repository)
        LøsningRiver(rapid, repository)
    }

    @Test
    fun `tar imot hendelse`() {
        rapid.sendTestMessage(hendelse.toJson())

        repository.hentMeldinger().shouldHaveSingleElement { it is Hendelse }
    }

    @Test
    fun `tar imot behov`() {
        rapid.sendTestMessage(behov.toJson())

        repository.hentMeldinger().shouldHaveSingleElement { it is Behov }
    }

    @Test
    fun `tar imot løsning`() {
        rapid.sendTestMessage(løsning.toJson())

        repository.hentMeldinger().shouldHaveSingleElement { it is Løsning }
    }
}
