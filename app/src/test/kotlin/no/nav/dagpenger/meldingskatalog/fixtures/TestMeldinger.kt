package no.nav.dagpenger.meldingskatalog.fixtures

import no.nav.helse.rapids_rivers.JsonMessage
import java.util.UUID

object TestMeldinger {
    val behovId = UUID.randomUUID()

    val hendelse =
        JsonMessage.newMessage("hendelseA", mapOf())

    fun behov(behov: List<String> = listOf("behovA")) =
        JsonMessage.newNeed(behov, mapOf("@behovId" to behovId)).apply {
            interestedIn("@id", "@opprettet", "@behovId", "@behov")
        }

    fun løsning(behov: List<String> = listOf("behovA")): JsonMessage {
        val løsninger = behov.associateWith { "løsning" }
        return JsonMessage.newNeed(behov, mapOf("@behovId" to behovId, "@løsning" to løsninger))
            .apply {
                interestedIn("@id", "@opprettet", "@behovId", "@behov", "@løsning")
            }
    }
}
