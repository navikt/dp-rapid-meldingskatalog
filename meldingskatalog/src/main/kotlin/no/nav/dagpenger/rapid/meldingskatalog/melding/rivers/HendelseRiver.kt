package no.nav.dagpenger.rapid.meldingskatalog.melding.rivers

import no.nav.dagpenger.rapid.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding.Hendelse
import no.nav.dagpenger.rapid.meldingskatalog.melding.kjede
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

private class HendelseRiver(
    rapidsConnection: RapidsConnection,
    meldingskatalog: Meldingskatalog,
    override val riverName: String,
) : IdentifisertMeldingRiver(rapidsConnection, meldingskatalog) {
    override val eventName: String = riverName
    override fun validate(message: JsonMessage) {}

    override fun createMessage(packet: JsonMessage) = Hendelse(
        navn = riverName,
        behandlingskjede = packet.kjede(),
        meldingsreferanseId = packet["@id"].asText().let(UUID::fromString),
        opprettet = packet["@opprettet"].asLocalDateTime(),
        mottatt = LocalDateTime.now(),
    )
}

fun Meldingskatalog.hendelse(navn: String) {
    HendelseRiver(delegatedRapid, this, navn)
}
