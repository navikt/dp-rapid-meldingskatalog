package no.nav.dagpenger.rapid.system

interface MessageProcessor {
    fun processMessage(jsonMessage: String): Boolean
}