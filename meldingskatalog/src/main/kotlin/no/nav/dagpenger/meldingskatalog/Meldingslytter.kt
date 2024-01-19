package no.nav.dagpenger.meldingskatalog

interface Meldingslytter {
    fun gjenkjentMelding(melding: Meldingsinformasjon)

    fun ukjentMelding(melding: String)
}
