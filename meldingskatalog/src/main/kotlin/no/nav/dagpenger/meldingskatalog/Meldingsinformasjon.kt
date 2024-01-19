package no.nav.dagpenger.meldingskatalog

import java.time.LocalDateTime
import java.util.UUID

fun interface MeldingsinformasjonBygger {
    fun bygg(
        type: Melding,
        json: String,
    ): Meldingsinformasjon
}

data class Meldingsinformasjon(
    val navn: String,
    val type: String,
    val behandlingskjede: Behandlingskjede,
    val meldingsreferanseId: UUID,
    val opprettet: LocalDateTime,
    val mottatt: LocalDateTime,
)

data class Tjeneste(val navn: String)

data class Behandlingskjede(
    private val kjede: MutableList<Tjeneste> = mutableListOf(),
) : MutableList<Tjeneste> by kjede {
    fun produsent() = kjede.first()
}
