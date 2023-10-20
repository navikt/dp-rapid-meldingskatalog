package no.nav.dagpenger.rapid.meldingskatalog.lyttere

import mu.KotlinLogging
import no.nav.dagpenger.rapid.meldingskatalog.Meldingslytter
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding

class Meldingsteller : Meldingslytter {
    private val identifiserteMeldinger = mutableMapOf<String, Int>()
    val kjenteMeldinger get() = identifiserteMeldinger.values.sum()
    var ukjenteMeldinger = 0
        private set

    fun kjenteMeldingerAvType(type: String) = identifiserteMeldinger.getOrDefault(type, 0)

    private fun tellMelding(melding: String) {
        identifiserteMeldinger.compute(melding) { _, count -> count?.plus(1) ?: 1 }
    }

    private fun tellUkjentMelding() = ++ukjenteMeldinger
    override fun gjenkjentMelding(melding: IdentifisertMelding) {
        tellMelding(melding.navn).also {
            logger.info {
                "Foreløpig antall meldinger: \n" +
                    identifiserteMeldinger.entries.sortedByDescending { it.value }.joinToString("\n") {
                        it.key + ": " + it.value
                    }
            }
        }
    }

    override fun ukjentMelding(melding: String) {
        tellUkjentMelding()
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
