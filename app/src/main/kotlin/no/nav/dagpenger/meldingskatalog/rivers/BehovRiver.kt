package no.nav.dagpenger.meldingskatalog.rivers

import no.nav.dagpenger.meldingskatalog.db.RapidMeldingRepository
import no.nav.dagpenger.meldingskatalog.melding.BehovMessage
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.BehovType
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Companion.EventNameKey
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Companion.LøsningKey
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Companion.NeedIdKey
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt.Companion.NeedKey
import no.nav.dagpenger.meldingskatalog.melding.Pakkeinnhold
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.toUUID

class BehovRiver(
    rapidsConnection: RapidsConnection,
    private val meldingRepository: RapidMeldingRepository,
) : River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue(EventNameKey, "behov")
                it.requireKey(NeedIdKey, NeedKey)
                it.forbid(LøsningKey)
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
                packet[NeedKey].map {
                    BehovType(behovId, it.asText())
                },
            )
        meldingRepository.lagreMelding(BehovMessage(konvolutt, innhold, packet.toJson()))
    }
}
