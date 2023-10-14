package no.nav.dagpenger.rapid.system

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class YourMessageProcessorImplementation() : MessageProcessor {
    companion object {
        val parser = jacksonObjectMapper()
    }

    override fun processMessage(jsonMessage: String): Boolean {
        val message = parser.readTree(jsonMessage)

        return message["type"].asText() == "knownMessageType"
    }
}