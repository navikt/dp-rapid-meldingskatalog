package no.nav.dagpenger.meldingskatalog.rivers

import no.nav.dagpenger.meldingskatalog.db.RapidMeldingRepository
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.LøsningType
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Companion.EventNameKey
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Companion.LøsningKey
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Companion.NeedIdKey
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Companion.NeedKey
import no.nav.dagpenger.meldingskatalog.melding.LøsningMessage
import no.nav.dagpenger.meldingskatalog.melding.Pakkeinnhold
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.toUUID

class LøsningRiver(
    rapidsConnection: RapidsConnection,
    private val meldingRepository: RapidMeldingRepository,
) : River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue(EventNameKey, "behov")
                it.requireKey(NeedIdKey, NeedKey)
                it.demandKey(LøsningKey)
                it.interestedIn("system_participating_services")
            }
        }.register(this)
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
    ) {
        val konvolutt = Konvolutt.fraMessage(packet)
        val behovId = packet[NeedIdKey].asText().toUUID()
        val innhold =
            Pakkeinnhold(
                packet[LøsningKey].fields().asSequence().map {
                    LøsningType(behovId, it.key)
                }.toList(),
            )
        meldingRepository.lagreMelding(LøsningMessage(konvolutt, innhold, packet.toJson()))
    }
}
