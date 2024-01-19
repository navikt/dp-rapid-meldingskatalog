package no.nav.dagpenger.rapid.meldingskatalog.rapid

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.meldingskatalog.KjentMelding
import no.nav.dagpenger.meldingskatalog.Melding
import no.nav.dagpenger.meldingskatalog.MeldingIdentifiseringStrategi
import no.nav.dagpenger.meldingskatalog.UkjentMelding

abstract class River protected constructor() : MeldingIdentifiseringStrategi {
    private companion object {
        private val jackson = jacksonObjectMapper()
    }

    override fun identifiser(melding: UkjentMelding) = identifiser(jackson.readTree(melding.json))

    abstract fun identifiser(melding: JsonNode): Boolean
}

class HendelseRiver internal constructor(private val eventName: String) : River() {
    private companion object {
        private val jackson = jacksonObjectMapper()
    }

    override fun identifiser(melding: JsonNode) = melding["@event_name"].textValue() == eventName
}

class BehovRiver internal constructor(private val behovName: String, private val eventName: String) : River() {
    private companion object {
        private val jackson = jacksonObjectMapper()
    }

    override fun identifiser(melding: JsonNode) =
        melding["@event_name"].textValue() == eventName &&
            melding["@behovId"].map { it.asText() }.contains(behovName)
}

fun hendelse(hendelseNavn: String) =
    KjentMelding(
        Melding(hendelseNavn, Melding.Meldingstype.Hendelse),
        HendelseRiver(hendelseNavn),
    )

fun behov(
    behovNavn: String,
    eventName: String = "behov",
) = KjentMelding(Melding(behovNavn, Melding.Meldingstype.Behov), BehovRiver(behovNavn, eventName))
