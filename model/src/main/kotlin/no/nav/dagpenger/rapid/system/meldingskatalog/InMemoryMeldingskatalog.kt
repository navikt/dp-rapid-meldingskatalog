package no.nav.dagpenger.rapid.system.meldingskatalog

import no.nav.dagpenger.rapid.system.melding.Melding

class InMemoryMeldingskatalog : Meldingskatalog {
    private val meldinger = mutableSetOf<Melding>()

    override fun leggTilMelding(melding: Melding): Boolean {
        if (meldinger.any { it.type == melding.type }) throw IllegalArgumentException("Melding med type ${melding.type} finnes allerede")
        return meldinger.add(melding)
    }

    override fun hentMeldingstyper() = meldinger.map { it.type }.toSet()

    override fun identifiser(jsonMessage: String) = meldinger.find { it.erMelding(jsonMessage) }
}
