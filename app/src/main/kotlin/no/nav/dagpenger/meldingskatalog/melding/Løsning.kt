package no.nav.dagpenger.meldingskatalog.melding

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.toUUID
import java.util.UUID

data class Løsning(
    val behovId: UUID,
    val løser: List<String>,
    private val konvolutt: Konvolutt,
) : Melding, Message by konvolutt {
    companion object {
        internal val LøsningKey = "@løsning"

        fun fraMessage(message: JsonMessage) =
            Løsning(
                message[Behov.NeedIdKey].asText().toUUID(),
                message[LøsningKey].fields().asSequence().map { it.key }.toList(),
                konvolutt = Konvolutt.fraMessage(message),
            )
    }
}
