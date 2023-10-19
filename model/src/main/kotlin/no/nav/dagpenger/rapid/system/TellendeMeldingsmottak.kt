package no.nav.dagpenger.rapid.system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.rapid.system.melding.Melding
import no.nav.dagpenger.rapid.system.meldingskatalog.Meldingskatalog

class TellendeMeldingsmottak(
    private val meldingskatalog: Meldingskatalog,
    private val alarmPublisher: AlarmPublisher,
) : Meldingsmottak {
    private val identifiserteMeldinger = mutableMapOf<String, Int>()

    companion object {
        val parser = jacksonObjectMapper()
    }

    override fun katalogiserMelding(jsonMessage: String): Boolean {
        val message: Melding? = meldingskatalog.identifiser(jsonMessage)

        if (message != null) {
            tellMeldingstype(message.type)
            return true
        } else {
            alarmPublisher.publishAlarm("Unknown message type")
            return false
        }
    }

    private fun tellMeldingstype(messageType: String) {
        identifiserteMeldinger.compute(messageType) { _, count -> count?.plus(1) ?: 1 }
    }

    override fun antallMeldinger(type: String) = identifiserteMeldinger.getOrDefault(type, 0)
}
