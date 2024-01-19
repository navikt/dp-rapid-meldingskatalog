package no.nav.dagpenger.rapid.meldingskatalog.service

import mu.KotlinLogging
import no.nav.dagpenger.meldingskatalog.Meldingsinformasjon
import no.nav.dagpenger.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.meldingskatalog.Meldingslytter
import no.nav.dagpenger.meldingskatalog.UkjentMelding
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

private val logger = KotlinLogging.logger {}

class MeldingRiver(
    rapidsConnection: RapidsConnection,
    private val meldingskatalog: Meldingskatalog,
    private val meldingslyttere: List<Meldingslytter>,
) : River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate {
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
        val json = packet.toJson()
        val melding = meldingskatalog.identifiser(UkjentMelding(json))

        when (melding) {
            is Meldingsinformasjon -> meldingslyttere.forEach { it.gjenkjentMelding(melding) }
            null -> meldingslyttere.forEach { it.ukjentMelding(json) }
        }
        logger.info { "identifiserte melding som $melding" }
    }
}
