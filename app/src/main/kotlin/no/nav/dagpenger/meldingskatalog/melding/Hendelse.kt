package no.nav.dagpenger.meldingskatalog.melding

import no.nav.helse.rapids_rivers.JsonMessage

data class Hendelse(
    val konvolutt: Konvolutt,
) : Melding, Message by konvolutt {
    val type = konvolutt.eventName

    companion object {
        fun fraMessage(message: JsonMessage) =
            Hendelse(
                konvolutt = Konvolutt.fraMessage(message),
            )
    }
}
