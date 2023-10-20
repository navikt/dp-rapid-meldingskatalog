package no.nav.dagpenger.rapid.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.melding.behov
import no.nav.dagpenger.rapid.meldingskatalog.melding.hendelse

fun Meldingskatalog.kjenteMeldinger() {
    behov("Journalpost")
    behov("NySøknad")
    behov("Barn", "faktum_svar") // Quiz sender behov med annen event type
    hendelse("faktum_svar")
    hendelse("søker_oppgave")
}
