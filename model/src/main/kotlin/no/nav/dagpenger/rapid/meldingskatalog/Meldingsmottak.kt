package no.nav.dagpenger.rapid.meldingskatalog

interface Meldingsmottak {
    fun katalogiserMelding(jsonMessage: String): Boolean
    fun antallMeldinger(type: String): Int
}
