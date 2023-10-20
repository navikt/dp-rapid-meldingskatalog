package no.nav.dagpenger.rapid.meldingskatalog.melding

import mu.KLogger

sealed class IdentifisertMelding(
    val navn: String,
) {
    class Behov(
        navn: String,
    ) : IdentifisertMelding(navn)

    class Hendelse(
        navn: String,
    ) : IdentifisertMelding(navn)

    internal fun loggIdentifisert(logger: KLogger) {
        logger.info { "gjenkjente pakke av type=${this::class.simpleName} med navn=$navn" }
    }
}
