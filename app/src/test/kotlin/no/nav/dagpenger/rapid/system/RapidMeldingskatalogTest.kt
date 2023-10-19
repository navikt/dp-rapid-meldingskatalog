package no.nav.dagpenger.rapid.system

import no.nav.dagpenger.rapid.system.alarm.KafkaAlarmPublisher
import no.nav.dagpenger.rapid.system.melding.HendelseMelding
import no.nav.dagpenger.rapid.system.melding.RapidsMelding
import no.nav.dagpenger.rapid.system.meldingskatalog.InMemoryMeldingskatalog
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RapidMeldingskatalogTest {
    private val rapid = TestRapid()

    @Test
    fun `Parser meldinger, teller kjente, og gir alarm på ukjente`() {
        val kafkaAlarmPublisher = KafkaAlarmPublisher(rapid)

        val messageCatalog = InMemoryMeldingskatalog().apply {
            leggTilMelding(HendelseMelding("typeA"))
            leggTilMelding(
                RapidsMelding("typeC").apply {
                    validate { it.requireValue("@event_name", "typeC") }
                },
            )
        }
        val processor = TellendeMeldingsmottak(messageCatalog, kafkaAlarmPublisher)

        processor.katalogiserMelding(typeJsonMessage)
        processor.katalogiserMelding(rapidJsonMessage)
        processor.katalogiserMelding(unknownJsonMessage)

        assertSame(processor.antallMeldinger("typeA"), 1)
        assertSame(rapid.inspektør.size, 1)
        assertEquals(rapid.inspektør.field(0, "message").asText(), "Unknown message type")
    }

    @Language("JSON")
    private val typeJsonMessage = """{"@event_name": "typeA", "data": {"key": "value"}}"""

    @Language("JSON")
    private val rapidJsonMessage = """{"@event_name": "typeC", "data": {"key": "value"}}"""

    @Language("JSON")
    private val unknownJsonMessage = """{"@event_name": "typeB", "data": {"key": "value"}}"""
}
