package no.nav.dagpenger.meldingskatalog.rivers

import no.nav.dagpenger.meldingskatalog.db.MeldingRepository
import no.nav.dagpenger.meldingskatalog.melding.Hendelse
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

class HendelseRiver(
    rapidsConnection: RapidsConnection,
    private val repository: MeldingRepository,
) : River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate {
                it.forbidValues(Konvolutt.EventNameKey, listOf("behov"))
                it.rejectValues(
                    "@event_name",
                    listOf(
                        "ping",
                        "pong",
                        "aktivitetslogg",
                        "app_status",
                        "application_ready",
                        "application_not_ready",
                        "application_stop",
                        "application_down",
                        "application_up",
                    ),
                )
                it.interestedIn("system_participating_services")
            }
        }.register(this)
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
    ) {
        val melding = Hendelse.fraMessage(packet)
        repository.lagre(packet, melding)
    }
}
