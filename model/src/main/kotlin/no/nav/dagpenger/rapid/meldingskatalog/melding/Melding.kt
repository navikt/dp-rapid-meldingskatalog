package no.nav.dagpenger.rapid.meldingskatalog.melding

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

interface Melding {
    val type: String
    fun erMelding(jsonMessage: String): Boolean
}

abstract class JsonMelding : Melding {
    protected companion object {
        val parser = jacksonObjectMapper()
    }
}
