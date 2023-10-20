package no.nav.dagpenger.rapid.meldingskatalog.melding

import no.nav.dagpenger.rapid.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding.Behov
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection

private class BehovRiver(
    rapidsConnection: RapidsConnection,
    meldingskatalog: Meldingskatalog,
    override val riverName: String,
    override val eventName: String = "behov",
) : IdentifisertMeldingRiver(rapidsConnection, meldingskatalog) {
    override fun validate(message: JsonMessage) {
        message.requireAllOrAny("@behov", listOf(riverName))
    }

    override fun createMessage(packet: JsonMessage) = Behov(riverName)
}

fun Meldingskatalog.behov(name: String, eventName: String = "behov") {
    BehovRiver(delegatedRapid, this, name, eventName)
}
