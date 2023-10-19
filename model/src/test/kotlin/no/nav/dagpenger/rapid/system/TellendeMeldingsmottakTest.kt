package no.nav.dagpenger.rapid.system

import no.nav.dagpenger.rapid.system.melding.HendelseMelding
import no.nav.dagpenger.rapid.system.meldingskatalog.InMemoryMeldingskatalog
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TellendeMeldingsmottakTest {
    @Test
    fun testMessageTypeRecognition() {
        // Create an instance of InMemoryMessageCatalog and add known message types
        val messageCatalog = InMemoryMeldingskatalog()
        messageCatalog.leggTilMelding(HendelseMelding("typeA"))
        messageCatalog.leggTilMelding(HendelseMelding("typeB"))

        // Create a MockAlarmPublisher for testing
        val alarmPublisher = MockAlarmPublisher()

        // Create an instance of KafkaMessageProcessor with dependencies
        val kafkaMessageProcessor = TellendeMeldingsmottak(messageCatalog, alarmPublisher)

        // Create sample JSON messages of known and unknown types
        val knownMessage = """{"@event_name": "typeA", "data": {"key": "value"}}"""
        val unknownMessage = """{"@event_name": "typeC", "data": {"key": "value"}}"""

        // Process the sample messages
        val knownMessageResult = kafkaMessageProcessor.katalogiserMelding(knownMessage)
        val unknownMessageResult = kafkaMessageProcessor.katalogiserMelding(unknownMessage)

        // Check if known message is processed and unknown message triggers an alarm
        assertTrue(knownMessageResult)
        assertTrue(!unknownMessageResult)

        // Check if the alarm was published for the unknown message
        assertEquals(alarmPublisher.getPublishedAlarm(), "Unknown message type")
    }
}

private class MockAlarmPublisher : AlarmPublisher {
    private var alarm: String = ""
    override fun publishAlarm(alarmtekst: String) {
        alarm = alarmtekst
    }

    fun getPublishedAlarm() = alarm
}
