package no.nav.dagpenger.meldingskatalog

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.meldingskatalog.behov.Behov
import no.nav.dagpenger.meldingskatalog.behov.BehovRepository
import no.nav.dagpenger.meldingskatalog.behov.BehovSporer
import no.nav.dagpenger.meldingskatalog.db.Meldingstype
import no.nav.dagpenger.meldingskatalog.db.Postgres.withMigratedDb
import no.nav.dagpenger.meldingskatalog.db.RapidMeldingRepository
import no.nav.dagpenger.meldingskatalog.db.RapidMeldingRepositoryObserver
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.behov
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.behovId
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.hendelse
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger.løsning
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype
import no.nav.dagpenger.meldingskatalog.melding.RapidMelding
import no.nav.dagpenger.meldingskatalog.rivers.BehovRiver
import no.nav.dagpenger.meldingskatalog.rivers.HendelseRiver
import no.nav.dagpenger.meldingskatalog.rivers.LøsningRiver
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test
import java.util.UUID

class E2ETest {
    private val rapid = TestRapid()
    private val behovRepository =
        object : BehovRepository {
            private val behovMap: MutableMap<UUID, Behov> = mutableMapOf()

            override fun hentBehov(behovId: UUID) = behovMap.getOrPut(behovId) { Behov(behovId) }

            override fun hentBehov() = behovMap.values.toList()

            override fun lagreBehov(behov: Behov) {
                TODO("Not yet implemented")
            }

            override fun medBehov(
                behovId: UUID,
                block: Behov.() -> Unit,
            ) {
                val behov = hentBehov(behovId)
                block(behov)
                // Lagre
            }
        }
    private val behovSporer = BehovSporer(behovRepository)
    private val meldingRepository =
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
        }.also {
            it.leggTilObserver(behovSporer)
        }

    init {
        HendelseRiver(rapid, meldingRepository)
        BehovRiver(rapid, meldingRepository)
        LøsningRiver(rapid, meldingRepository)
    }

    @Test
    fun `tar imot meldinger og lager kataloger over system og meldingstyper`() {
        withMigratedDb {
            rapid.sendTestMessage(hendelse.toJson())
            rapid.sendTestMessage(behov().toJson())
            rapid.sendTestMessage(løsning().toJson())

            meldingRepository.hentMeldinger() shouldHaveSize 3

            with(behovRepository.hentBehov(behovId)) {
                løst() shouldBe true
            }
        }
    }
}
