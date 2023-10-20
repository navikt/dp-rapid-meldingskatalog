package no.nav.dagpenger.rapid.meldingskatalog.lyttere

import mu.KotlinLogging
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding
import no.nav.dagpenger.rapid.meldingskatalog.Meldingslytter

class Meldingslogger : Meldingslytter {
    private companion object {
        val logger = KotlinLogging.logger {}
    }

    override fun gjenkjentMelding(melding: IdentifisertMelding) {
        logger.info("Gjenkjent melding: ${melding.navn}")
    }

    override fun ukjentMelding(melding: String) {
        logger.warn("Ukjent melding: $melding")
    }
}
