package no.nav.dagpenger.meldingskatalog.rivers

import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import no.nav.dagpenger.meldingskatalog.db.Meldingstype
import no.nav.dagpenger.meldingskatalog.db.RapidMeldingRepository
import no.nav.dagpenger.meldingskatalog.db.RapidMeldingRepositoryObserver
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.behov
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.hendelse
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.løsning
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.BehovType
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.HendelseType
import no.nav.dagpenger.meldingskatalog.melding.RapidMelding
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class RiverTest {
    private val rapid = TestRapid()
    private val repository =
        object : RapidMeldingRepository {
            private val observers = mutableSetOf<RapidMeldingRepositoryObserver>()
            private val meldinger = mutableListOf<RapidMelding<*>>()

            override fun <T : Innholdstype> lagreMelding(melding: RapidMelding<T>) {
                meldinger.add(melding)
                observers.forEach { it.onMessageAdded(melding) }
            }

            override fun hentMeldinger() = meldinger

            override fun hentMelding(meldingsreferanseId: UUID) =
                meldinger.first { it.meldingsreferanseId == meldingsreferanseId } as RapidMelding<Innholdstype>

            override fun leggTilObserver(observer: RapidMeldingRepositoryObserver) = observers.add(observer)

            override fun hentMeldingstyper(): List<Meldingstype> {
                TODO("Not yet implemented")
            }

            fun reset() = meldinger.clear()
        }

    init {
        HendelseRiver(rapid, repository)
        BehovRiver(rapid, repository)
        LøsningRiver(rapid, repository)
    }

    @BeforeEach
    fun setup() {
        repository.reset()
    }

    @Test
    fun `tar imot hendelse`() {
        rapid.sendTestMessage(hendelse.toJson())

        repository.hentMeldinger().shouldHaveSingleElement { it.type is HendelseType }
    }

    @Test
    fun `tar imot behov`() {
        rapid.sendTestMessage(behov().toJson())

        repository.hentMeldinger().shouldHaveSingleElement { it.type is BehovType }
    }

    @Test
    fun `tar imot løsning`() {
        rapid.sendTestMessage(behov().toJson())
        rapid.sendTestMessage(løsning().toJson())

        repository.hentMeldinger().shouldHaveSize(2)
    }
}
