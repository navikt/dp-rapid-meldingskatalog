package no.nav.dagpenger.rapid.meldingskatalog.melding.rivers

import no.nav.dagpenger.rapid.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding.Behov
import no.nav.dagpenger.rapid.meldingskatalog.melding.kjede
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

private class BehovRiver(
    rapidsConnection: RapidsConnection,
    meldingskatalog: Meldingskatalog,
    override val riverName: String,
    override val eventName: String = "behov",
) : IdentifisertMeldingRiver(rapidsConnection, meldingskatalog) {
    override fun validate(message: JsonMessage) {
        message.requireAllOrAny("@behov", listOf(riverName))
    }

    override fun createMessage(packet: JsonMessage) = Behov(
        navn = riverName,
        behandlingskjede = packet.kjede(),
        meldingsreferanseId = packet["@id"].asText().let(UUID::fromString),
        opprettet = packet["@opprettet"].asLocalDateTime(),
        mottatt = LocalDateTime.now(),
    )
}

fun Meldingskatalog.behov(name: String, eventName: String = "behov") {
    BehovRiver(delegatedRapid, this, name, eventName)
}
