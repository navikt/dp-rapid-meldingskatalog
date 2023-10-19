package no.nav.dagpenger.rapid.system

import no.nav.dagpenger.rapid.system.dsl.MeldingskatalogDSL
import no.nav.dagpenger.rapid.system.melding.river
import no.nav.dagpenger.rapid.system.meldingskatalog.InMemoryMeldingskatalog

val kjenteMeldinger = MeldingskatalogDSL(InMemoryMeldingskatalog()) {
    hendelse("vedtak_fattet")
    behov("vedtak_fattet")
    river("foo") {
        validate { it.requireValue("@event_name", "foo") }
    }
}.meldingskatalog
