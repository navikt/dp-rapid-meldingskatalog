package no.nav.dagpenger.rapid.meldingskatalog.melding

import no.nav.helse.rapids_rivers.JsonMessage
import java.time.LocalDateTime
import java.util.LinkedList
import java.util.UUID

sealed class IdentifisertMelding(
    val navn: String,
    val behandlingskjede: Behandlingskjede,
    val meldingsreferanseId: UUID,
    val opprettet: LocalDateTime,
    val mottatt: LocalDateTime,
) {
    abstract val type: String

    class Behov(
        navn: String,
        behandlingskjede: Behandlingskjede,
        meldingsreferanseId: UUID,
        opprettet: LocalDateTime,
        mottatt: LocalDateTime,
    ) : IdentifisertMelding(navn, behandlingskjede, meldingsreferanseId, opprettet, mottatt) {
        override val type: String = this::class.java.simpleName
    }

    class Hendelse(
        navn: String,
        behandlingskjede: Behandlingskjede,
        meldingsreferanseId: UUID,
        opprettet: LocalDateTime,
        mottatt: LocalDateTime,
    ) : IdentifisertMelding(navn, behandlingskjede, meldingsreferanseId, opprettet, mottatt) {
        override val type: String = this::class.java.simpleName
    }
}

typealias Behandlingskjede = LinkedList<System>

data class System(val navn: String)

fun JsonMessage.kjede() = this["system_participating_services"].map {
    System(it["service"]?.asText() ?: "test")
}.fold(Behandlingskjede()) { acc, system ->
    acc.add(system)
    acc
}
