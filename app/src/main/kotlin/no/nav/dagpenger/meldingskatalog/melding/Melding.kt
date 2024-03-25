package no.nav.dagpenger.meldingskatalog.melding

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.helse.rapids_rivers.toUUID
import java.time.LocalDateTime
import java.util.UUID

sealed interface Melding : Message

interface Message {
    val meldingsreferanseId: UUID
    val opprettet: LocalDateTime
    val eventName: String
    val sporing: List<Konvolutt.Sporing>
}

data class Konvolutt(
    override val meldingsreferanseId: UUID,
    override val opprettet: LocalDateTime,
    override val eventName: String,
    override val sporing: List<Sporing>,
) : Message {
    companion object {
        internal val OpprettetKey = "@opprettet"
        internal val EventNameKey = "@event_name"
        internal val ReadCountKey = "system_read_count"
        internal val ParticipatingServicesKey = "system_participating_services"

        fun fraMessage(message: JsonMessage): Konvolutt {
            message.requireKey(OpprettetKey, EventNameKey, ParticipatingServicesKey)
            val konvolutt =
                Konvolutt(
                    message.id.toUUID(),
                    message[OpprettetKey].asLocalDateTime(),
                    message[EventNameKey].asText(),
                    message[ParticipatingServicesKey].map(Sporing.Companion::fraJsonNode),
                )
            return konvolutt
        }
    }

    data class Sporing(
        val id: UUID,
        val time: LocalDateTime,
        val service: String?,
        val instance: String?,
        val image: String?,
    ) {
        companion object {
            fun fraJsonNode(it: JsonNode) =
                Sporing(
                    id = it["id"].asText().toUUID(),
                    time = it["time"].asLocalDateTime(),
                    service = it["service"]?.asText(),
                    instance = it["instance"]?.asText(),
                    image = it["image"]?.asText(),
                )
        }
    }
}
