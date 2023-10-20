package no.nav.dagpenger.rapid.meldingskatalog.melding.rivers

import no.nav.dagpenger.rapid.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding.Hendelse
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection

private class HendelseRiver(
    rapidsConnection: RapidsConnection,
    meldingskatalog: Meldingskatalog,
    override val riverName: String,
) : IdentifisertMeldingRiver(rapidsConnection, meldingskatalog) {
    override val eventName: String = riverName
    override fun validate(message: JsonMessage) {}

    override fun createMessage(packet: JsonMessage) = Hendelse(riverName)
}

fun Meldingskatalog.hendelse(navn: String) {
    HendelseRiver(delegatedRapid, this, navn)
}
