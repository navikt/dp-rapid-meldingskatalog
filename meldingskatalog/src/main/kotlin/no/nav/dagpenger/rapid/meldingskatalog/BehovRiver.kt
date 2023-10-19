package no.nav.dagpenger.rapid.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.IdentifisertMelding.Behov
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection

private class BehovRiver(
    rapidsConnection: RapidsConnection,
    meldingskatalog: Meldingskatalog,
    override val riverName: String,
) : IdentifisertMeldingRiver(rapidsConnection, meldingskatalog) {
    override val eventName: String = "behov"
    override fun validate(message: JsonMessage) {
        message.requireAllOrAny("@behov", listOf(riverName))
    }

    override fun createMessage(packet: JsonMessage) = Behov(riverName)
}

fun Meldingskatalog.behov(name: String) {
    BehovRiver(delegatedRapid, this, name)
}
