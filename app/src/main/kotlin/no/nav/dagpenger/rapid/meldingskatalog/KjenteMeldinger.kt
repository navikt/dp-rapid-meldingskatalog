package no.nav.dagpenger.rapid.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.rapid.behov
import no.nav.dagpenger.rapid.meldingskatalog.rapid.hendelse

val kjenteMeldinger =
    listOf(
        behov("Journalpost"),
        behov("NySøknad"),
        // Quiz sender behov med annen event type
        behov("Barn", "faktum_svar"),
        hendelse("faktum_svar"),
        hendelse("søker_oppgave"),
        hendelse("dokumentkrav_innsendt"),
        hendelse("søknad_endret_tilstand"),
        behov("NyInnsending"),
        behov("InnsendingMetadata"),
        behov("ArkiverbarSøknad"),
        behov("Persondata"),
        behov("MinsteinntektVurdering"),
        behov("EksisterendeSaker"),
        behov("OpprettStartVedtakOppgave"),
        behov("OpprettGosysoppgave"),
        behov("OppdaterJournalpost"),
        hendelse("manuell_behandling"),
    )
