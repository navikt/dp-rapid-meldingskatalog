package no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog

import no.nav.dagpenger.meldingskatalog.Meldingsinformasjon
import java.time.LocalDateTime
import java.util.UUID

internal interface MeldingRepository {
    fun lagre(melding: Meldingsinformasjon)

    fun hentMeldingstyper(): List<Meldingstype>

    fun hentMeldingstype(meldingstype: String): Meldingstype

    fun hentMeldinger(meldingstype: String): List<Melding>

    fun hentSystem(): List<System>
}

data class Meldingstype(val navn: String, val type: String, val antall: Int, val system: List<String>)

data class Melding(
    val meldingsreferanseId: UUID,
    val navn: String,
    val type: String,
    val opprettet: LocalDateTime,
    val mottatt: LocalDateTime,
)

data class System(val navn: String, val meldingstyper: List<Meldingstype>)
