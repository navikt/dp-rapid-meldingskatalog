package no.nav.dagpenger.rapid.meldingskatalog.dsl

import no.nav.dagpenger.rapid.meldingskatalog.melding.BehovMelding
import no.nav.dagpenger.rapid.meldingskatalog.melding.HendelseMelding
import no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog.Meldingskatalog

class MeldingskatalogDSL(val meldingskatalog: Meldingskatalog, val dsl: MeldingskatalogDSL.() -> Unit) {
    fun behov(type: String) = meldingskatalog.leggTilMelding(BehovMelding(type))

    fun hendelse(type: String) = meldingskatalog.leggTilMelding(HendelseMelding(type))
}
