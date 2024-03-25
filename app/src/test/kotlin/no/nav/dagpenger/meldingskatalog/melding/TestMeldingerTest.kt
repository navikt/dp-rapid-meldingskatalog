package no.nav.dagpenger.meldingskatalog.melding

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.dagpenger.meldingskatalog.db.MeldingRepositoryInMemory
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.behov
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.hendelse
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.løsning
import org.junit.jupiter.api.Test

class TestMeldingerTest {
    private val repository = MeldingRepositoryInMemory()

    @Test
    fun `kan ta i mot hendelser`() {
        val melding = Hendelse.fraMessage(hendelse)
        melding.shouldBeInstanceOf<Hendelse>()
        melding.meldingsreferanseId.shouldNotBeNull()
        melding.opprettet.shouldNotBeNull()
        melding.eventName shouldBe "hendelseA"

        repository.lagre(hendelse, melding)
    }

    @Test
    fun `kan ta i mot behov`() {
        val melding = Behov.fraMessage(behov)
        melding.shouldBeInstanceOf<Behov>()
        melding.meldingsreferanseId.shouldNotBeNull()
        melding.opprettet.shouldNotBeNull()
        melding.eventName shouldBe "behov"
        melding.behov shouldContain "behovA"

        repository.lagre(behov, melding)
    }

    @Test
    fun `kan ta i mot løsning`() {
        val melding = Løsning.fraMessage(løsning)
        melding.shouldBeInstanceOf<Løsning>()
        melding.meldingsreferanseId.shouldNotBeNull()
        melding.opprettet.shouldNotBeNull()
        melding.eventName shouldBe "behov"
        melding.løser shouldContain "behovA"

        repository.lagre(løsning, melding)
    }
}
