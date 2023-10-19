package no.nav.dagpenger.rapid.system

import no.nav.dagpenger.rapid.system.alarm.KafkaAlarmPublisher
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class KafkaAlarmPublisherTest {
    private val rapid = TestRapid()

    @Test
    fun testPublishAlarmToKafka() {
        val alarmMessage = "This is an alarm message"

        val kafkaAlarmPublisher = KafkaAlarmPublisher(rapid)

        // Publish an alarm message
        kafkaAlarmPublisher.publishAlarm(alarmMessage)

        assertSame(rapid.inspektør.size, 1)
        assertEquals(rapid.inspektør.field(0, "message").asText(), alarmMessage)
    }
}
