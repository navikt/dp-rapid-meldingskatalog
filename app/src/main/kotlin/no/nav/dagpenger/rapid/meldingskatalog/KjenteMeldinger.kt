package no.nav.dagpenger.rapid.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.melding.rivers.behov
import no.nav.dagpenger.rapid.meldingskatalog.melding.rivers.hendelse

fun Meldingskatalog.kjenteMeldinger() {
    behov("Journalpost")
    behov("NySøknad")
    behov("Barn", "faktum_svar") // Quiz sender behov med annen event type
    hendelse("faktum_svar")
    hendelse("søker_oppgave")
    hendelse("dokumentkrav_innsendt")
    hendelse("søknad_endret_tilstand")
    behov("NyInnsending")
    behov("InnsendingMetadata")
    behov("ArkiverbarSøknad")
    behov("Persondata")
    behov("MinsteinntektVurdering")
    behov("EksisterendeSaker")
    behov("OpprettStartVedtakOppgave")
    behov("OpprettGosysoppgave")
    behov("OppdaterJournalpost")
    hendelse("manuell_behandling")
    hendelse("søknad_innsendt")
}
