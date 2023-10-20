package no.nav.dagpenger.rapid.meldingskatalog.melding

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.asLocalDateTime
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals

class IdentifisertMeldingTest {
    @Test
    fun foobar() {
        val packet = JsonMessage.newMessage(
            "foobar",
            mapOf(
                "system_participating_services" to listOf(
                    mapOf("service" to "tjeneste1"),
                    mapOf("service" to "tjeneste2"),
                    mapOf("service" to "tjeneste3"),
                    mapOf("service" to "tjeneste4"),
                ),
            ),
        ).also {
            it.interestedIn("system_participating_services")
        }
        val behov = IdentifisertMelding.Behov(
            "foobar",
            packet.kjede(),
            packet["@id"].asText().let(UUID::fromString),
            packet["@opprettet"].asLocalDateTime(),
            LocalDateTime.now(),
        )

        assertEquals("foobar", behov.navn)
        assertEquals("tjeneste1", behov.behandlingskjede[0].navn)
        assertEquals("tjeneste2", behov.behandlingskjede[1].navn)
        assertEquals("tjeneste3", behov.behandlingskjede[2].navn)
        assertEquals("tjeneste4", behov.behandlingskjede[3].navn)
    }
}
