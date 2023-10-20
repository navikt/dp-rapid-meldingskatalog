package no.nav.dagpenger.rapid.meldingskatalog

import mu.KotlinLogging
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.helse.rapids_rivers.RapidsConnection

class Meldingskatalog(
    rapidsConnection: RapidsConnection,
) {
    private companion object {
        private val sikkerlogg = KotlinLogging.logger("tjenestekall")
    }

    internal val delegatedRapid = DelegatedRapid(rapidsConnection)

    private val listeners = mutableSetOf<Meldingslytter>()
    private var messageRecognized = false

    private val riverErrors = mutableListOf<Pair<String, MessageProblems>>()

    fun leggTilLytter(listener: Meldingslytter) {
        listeners.add(listener)
    }

    private fun beforeRiverHandling() {
        messageRecognized = false
        riverErrors.clear()
    }

    internal fun onRecognizedMessage(message: IdentifisertMelding, context: MessageContext) {
        messageRecognized = true
        listeners.forEach { it.gjenkjentMelding(message) }
    }

    internal fun onRiverError(riverName: String, problems: MessageProblems, context: MessageContext) {
        riverErrors.add(riverName to problems)
    }

    private fun afterRiverHandling(message: String) {
        if (messageRecognized || riverErrors.isEmpty()) return
        listeners.forEach { it.ukjentMelding(message, riverErrors) }
    }

    internal inner class DelegatedRapid(
        private val rapidsConnection: RapidsConnection,
    ) : RapidsConnection(), RapidsConnection.MessageListener {

        init {
            rapidsConnection.register(this)
        }

        override fun onMessage(message: String, context: MessageContext) {
            beforeRiverHandling()
            notifyMessage(message, context)
            afterRiverHandling(message)
        }

        override fun publish(message: String) {
            rapidsConnection.publish(message)
        }

        override fun publish(key: String, message: String) {
            rapidsConnection.publish(key, message)
        }

        override fun rapidName() = rapidsConnection.rapidName()

        override fun start() = throw IllegalStateException()
        override fun stop() = throw IllegalStateException()
    }
}

interface Meldingslytter {
    fun gjenkjentMelding(melding: IdentifisertMelding)

    fun ukjentMelding(melding: String, riverErrors: MutableList<Pair<String, MessageProblems>>)
}
