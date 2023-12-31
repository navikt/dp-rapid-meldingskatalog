package no.nav.dagpenger.rapid.meldingskatalog.melding.rivers

import com.fasterxml.jackson.databind.JsonNode
import mu.withLoggingContext
import no.nav.dagpenger.rapid.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.util.UUID

internal abstract class IdentifisertMeldingRiver(
    rapidsConnection: RapidsConnection,
    private val meldingskatalog: Meldingskatalog,
) : River.PacketValidation {
    private val river = River(rapidsConnection).apply {
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
    }
    protected abstract val eventName: String
    protected abstract val riverName: String

    init {
        RiverImpl(river)
    }

    private fun validateHendelse(packet: JsonMessage) {
        packet.requireValue("@event_name", eventName)
        packet.require("@opprettet", JsonNode::asLocalDateTime)
        packet.require("@id") { UUID.fromString(it.asText()) }
    }

    protected abstract fun createMessage(packet: JsonMessage): IdentifisertMelding

    private inner class RiverImpl(river: River) : River.PacketListener {
        init {
            river.validate(::validateHendelse)
            river.validate(this@IdentifisertMeldingRiver)
            river.register(this)
        }

        override fun name() = this@IdentifisertMeldingRiver::class.simpleName ?: "ukjent"

        override fun onPacket(packet: JsonMessage, context: MessageContext) {
            withLoggingContext(
                "river_name" to riverName,
                "melding_type" to eventName,
                "melding_id" to packet["@id"].asText(),
            ) {
                meldingskatalog.onRecognizedMessage(createMessage(packet), context)
            }
        }

        override fun onError(problems: MessageProblems, context: MessageContext) {
            meldingskatalog.onRiverError(riverName, problems, context)
        }
    }
}
