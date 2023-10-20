package no.nav.dagpenger.rapid.meldingskatalog.melding.rivers

import no.nav.dagpenger.rapid.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding.Hendelse
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

private class MeldingRiver(
    rapidsConnection: RapidsConnection,
    meldingskatalog: Meldingskatalog,
    override val riverName: String,
    private val validation: List<River.PacketValidation>,
) : IdentifisertMeldingRiver(rapidsConnection, meldingskatalog) {
    override val eventName: String = riverName
    override fun validate(message: JsonMessage) {
        validation.forEach { it.validate(message) }
    }

    override fun createMessage(packet: JsonMessage) = Hendelse(riverName)
}

fun Meldingskatalog.melding(navn: String, vararg validation: River.PacketValidation) {
    MeldingRiver(delegatedRapid, this, navn, validation.toList())
}
