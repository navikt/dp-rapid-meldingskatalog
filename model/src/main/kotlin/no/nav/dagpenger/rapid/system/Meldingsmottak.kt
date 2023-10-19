package no.nav.dagpenger.rapid.system

interface Meldingsmottak {
    fun katalogiserMelding(jsonMessage: String): Boolean
    fun antallMeldinger(type: String): Int
}
