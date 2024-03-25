package no.nav.dagpenger.meldingskatalog.fixtures

import no.nav.helse.rapids_rivers.JsonMessage

object TestMeldinger {
    val hendelse =
        JsonMessage.newMessage("hendelseA", mapOf())
    val behov =
        JsonMessage.newNeed(listOf("behovA"), mapOf()).apply {
            interestedIn("@id", "@opprettet", "@behovId", "@behov")
        }
    val løsning =
        JsonMessage.newNeed(listOf("behovA"), mapOf("@løsning" to mapOf("behovA" to "løsningA"))).apply {
            interestedIn("@id", "@opprettet", "@behovId", "@behov", "@løsning")
        }
}
