package no.nav.dagpenger.meldingskatalog.rivers

import no.nav.dagpenger.meldingskatalog.db.MeldingRepository
import no.nav.dagpenger.meldingskatalog.melding.Behov
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.dagpenger.meldingskatalog.melding.Løsning
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

class LøsningRiver(
    rapidsConnection: RapidsConnection,
    private val repository: MeldingRepository,
) : River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue(Konvolutt.EventNameKey, "behov")
                it.requireKey(Behov.NeedIdKey, Behov.NeedKey)
                it.demandKey(Løsning.LøsningKey)
                it.interestedIn("system_participating_services")
            }
        }.register(this)
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
    ) {
        val melding = Løsning.fraMessage(packet)
        repository.lagre(packet, melding)
    }
}
