package no.nav.dagpenger.rapid.meldingskatalog.rapid

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.meldingskatalog.Behandlingskjede
import no.nav.dagpenger.meldingskatalog.Melding
import no.nav.dagpenger.meldingskatalog.Meldingsinformasjon
import no.nav.dagpenger.meldingskatalog.MeldingsinformasjonBygger
import no.nav.dagpenger.meldingskatalog.Tjeneste
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

class JsonMessageBygger : MeldingsinformasjonBygger {
    private companion object {
        private val jackson = jacksonObjectMapper()
    }

    override fun bygg(
        type: Melding,
        json: String,
    ): Meldingsinformasjon {
        val packet = jackson.readTree(json)
        return Meldingsinformasjon(
            navn = type.navn,
            type = type.meldingstype.name,
            behandlingskjede = packet.kjede(),
            meldingsreferanseId = packet["@id"].asText().let(UUID::fromString),
            opprettet = packet["@opprettet"].asLocalDateTime(),
            mottatt = LocalDateTime.now(),
        )
    }

    private fun JsonNode.kjede() =
        this["system_participating_services"].map {
            Tjeneste(it["service"]?.asText() ?: "test")
        }.fold(Behandlingskjede()) { acc, system ->
            acc.add(system)
            acc
        }
}
