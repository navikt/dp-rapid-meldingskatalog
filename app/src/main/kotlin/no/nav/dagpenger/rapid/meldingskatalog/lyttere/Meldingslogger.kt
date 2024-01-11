package no.nav.dagpenger.rapid.meldingskatalog.lyttere

import mu.KotlinLogging
import no.nav.dagpenger.rapid.meldingskatalog.Meldingslytter
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding
import no.nav.helse.rapids_rivers.MessageProblems

class Meldingslogger : Meldingslytter {
    private companion object {
        val logger = KotlinLogging.logger {}
        val sikkerlogg = KotlinLogging.logger {"tjenestekall"}
    }

    override fun gjenkjentMelding(melding: IdentifisertMelding) {
        logger.info("Gjenkjent melding: ${melding.navn}")
    }

    override fun ukjentMelding(melding: String, riverErrors: MutableList<Pair<String, MessageProblems>>) {
        logger.warn("Ukjent melding. Se sikkerlogg for detaljer.")
        sikkerlogg.warn("Ukjent melding: $melding")
    }
}
