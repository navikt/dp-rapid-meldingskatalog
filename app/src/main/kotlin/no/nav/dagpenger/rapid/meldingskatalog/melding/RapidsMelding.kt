package no.nav.dagpenger.rapid.meldingskatalog.melding

import no.nav.dagpenger.rapid.meldingskatalog.dsl.MeldingskatalogDSL
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.helse.rapids_rivers.River

class RapidsMelding(override val type: String) : JsonMelding() {
    private val validations = mutableListOf<River.PacketValidation>()

    fun validate(validation: River.PacketValidation): RapidsMelding {
        validations.add(validation)
        return this
    }

    override fun erMelding(jsonMessage: String): Boolean {
        val problems = MessageProblems(jsonMessage)
        val packet = JsonMessage(jsonMessage, problems)
        validations.forEach { it.validate(packet) }
        return !problems.hasErrors()
    }
}

fun MeldingskatalogDSL.river(navn: String, validation: RapidsMelding.() -> Unit) = RapidsMelding(navn).apply(validation)
