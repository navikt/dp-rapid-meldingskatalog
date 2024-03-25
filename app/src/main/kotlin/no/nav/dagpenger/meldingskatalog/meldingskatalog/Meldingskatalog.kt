package no.nav.dagpenger.meldingskatalog.meldingskatalog

import no.nav.dagpenger.meldingskatalog.melding.Melding

interface Meldingskatalog {
    fun identifiser(melding: Melding) {
    }
}

enum class Meldingkategori {
    BEHOV,
    LØSNING,
    HENDELSE,
}

interface Meldingstype {
    val kategori: Meldingkategori
    val navn: String
}
