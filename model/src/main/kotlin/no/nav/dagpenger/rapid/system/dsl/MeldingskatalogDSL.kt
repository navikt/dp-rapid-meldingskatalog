package no.nav.dagpenger.rapid.system.dsl

import no.nav.dagpenger.rapid.system.melding.BehovMelding
import no.nav.dagpenger.rapid.system.melding.HendelseMelding
import no.nav.dagpenger.rapid.system.meldingskatalog.Meldingskatalog

class MeldingskatalogDSL(val meldingskatalog: Meldingskatalog, val dsl: MeldingskatalogDSL.() -> Unit) {
    fun behov(type: String) = meldingskatalog.leggTilMelding(BehovMelding(type))

    fun hendelse(type: String) = meldingskatalog.leggTilMelding(HendelseMelding(type))
}
