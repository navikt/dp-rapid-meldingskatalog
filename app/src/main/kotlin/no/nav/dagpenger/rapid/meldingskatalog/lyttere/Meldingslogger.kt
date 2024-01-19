package no.nav.dagpenger.rapid.meldingskatalog.lyttere

import mu.KotlinLogging
import no.nav.dagpenger.meldingskatalog.Meldingsinformasjon
import no.nav.dagpenger.meldingskatalog.Meldingslytter

class Meldingslogger : Meldingslytter {
    private companion object {
        val logger = KotlinLogging.logger {}
        val sikkerlogg = KotlinLogging.logger {}
    }

    override fun gjenkjentMelding(melding: Meldingsinformasjon) {
        logger.info("Gjenkjent melding: ${melding.navn}")
    }

    override fun ukjentMelding(melding: String) {
        logger.warn("Ukjent melding. Se sikkerlogg for detaljer.")
        sikkerlogg.warn("Ukjent melding: $melding")
    }
}
