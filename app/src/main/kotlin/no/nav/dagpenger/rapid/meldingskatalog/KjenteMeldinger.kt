package no.nav.dagpenger.rapid.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.dsl.MeldingskatalogDSL
import no.nav.dagpenger.rapid.meldingskatalog.melding.river
import no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog.InMemoryMeldingskatalog

val kjenteMeldinger = MeldingskatalogDSL(InMemoryMeldingskatalog()) {
    hendelse("vedtak_fattet")
    behov("vedtak_fattet")
    river("foo") {
        validate { it.requireValue("@event_name", "foo") }
    }
}.meldingskatalog
