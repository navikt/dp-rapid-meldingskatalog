package no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.melding.Melding

interface Meldingskatalog {
    fun leggTilMelding(melding: Melding): Boolean
    fun hentMeldingstyper(): Set<String>
    fun identifiser(jsonMessage: String): Melding?
}
