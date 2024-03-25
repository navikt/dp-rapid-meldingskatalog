package no.nav.dagpenger.meldingskatalog.melding

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.toUUID
import java.util.UUID

data class Behov(
    val behovId: UUID,
    val behov: List<String>,
    private val konvolutt: Konvolutt,
) : Melding, Message by konvolutt {
    companion object {
        internal val NeedIdKey = "@behovId"
        internal val NeedKey = "@behov"

        fun fraMessage(message: JsonMessage) =
            Behov(
                message[NeedIdKey].asText().toUUID(),
                message[NeedKey].map { it.asText() },
                konvolutt = Konvolutt.fraMessage(message),
            )
    }
}
