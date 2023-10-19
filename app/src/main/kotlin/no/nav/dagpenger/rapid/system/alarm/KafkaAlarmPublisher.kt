package no.nav.dagpenger.rapid.system.alarm

import no.nav.dagpenger.rapid.system.AlarmPublisher
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection

class KafkaAlarmPublisher(private val rapidsConnection: RapidsConnection) : AlarmPublisher {
    override fun publishAlarm(alarmTekst: String) {
        val message = JsonMessage.newMessage("ukjent_melding", mutableMapOf("message" to alarmTekst))
        rapidsConnection.publish(message.toJson())
    }
}
