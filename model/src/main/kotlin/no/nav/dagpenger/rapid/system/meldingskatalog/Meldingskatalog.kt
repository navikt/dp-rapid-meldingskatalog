package no.nav.dagpenger.rapid.system.meldingskatalog

import no.nav.dagpenger.rapid.system.melding.Melding

interface Meldingskatalog {
    fun leggTilMelding(melding: Melding): Boolean
    fun hentMeldingstyper(): Set<String>
    fun identifiser(jsonMessage: String): Melding?
}
