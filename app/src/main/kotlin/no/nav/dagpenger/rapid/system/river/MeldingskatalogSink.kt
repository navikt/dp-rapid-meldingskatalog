package no.nav.dagpenger.rapid.system.river

import no.nav.dagpenger.rapid.system.Meldingsmottak
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

internal class MeldingskatalogSink(
    rapidsConnection: RapidsConnection,
    private val meldingsmottak: Meldingsmottak,
) : River.PacketListener {
    init {
        River(rapidsConnection).validate {
            it.rejectValue("@event_name", "ukjent_melding")
            it.rejectValue("@event_name", "ping")
            it.rejectValue("@event_name", "aktivitetslogg")
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        meldingsmottak.katalogiserMelding(packet.toJson())
    }
}
