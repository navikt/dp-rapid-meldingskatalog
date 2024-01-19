package no.nav.dagpenger.meldingskatalog

fun interface MeldingIdentifiseringStrategi {
    fun identifiser(melding: UkjentMelding): Boolean
}
