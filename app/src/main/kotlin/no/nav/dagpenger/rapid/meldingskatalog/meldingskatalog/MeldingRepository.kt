package no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding
import java.time.LocalDateTime
import java.util.UUID

internal interface MeldingRepository {
    fun lagre(melding: IdentifisertMelding)
    fun hentMeldingstyper(): List<Meldingstype>
    fun hentMeldingstype(meldingstype: String): Meldingstype
    fun hentMeldinger(meldingstype: String): List<Melding>
    fun hentSystem(): List<System>
}

data class Meldingstype(val navn: String, val type: String, val antall: Int)
data class Melding(
    val meldingsreferanseId: UUID,
    val navn: String,
    val type: String,
    val opprettet: LocalDateTime,
    val mottatt: LocalDateTime,
)

data class System(val navn: String, val meldingstyper: List<Meldingstype>)
