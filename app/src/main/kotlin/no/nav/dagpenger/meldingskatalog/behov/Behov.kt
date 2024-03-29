package no.nav.dagpenger.meldingskatalog.behov

import java.time.LocalDateTime
import java.util.UUID

class Behov(
    val behovId: UUID,
    val behov: MutableSet<String> = mutableSetOf(),
    val løsning: MutableSet<String> = mutableSetOf(),
    val opprettet: LocalDateTime = LocalDateTime.now(),
    ferdig: LocalDateTime? = null,
    val meldinger: MutableSet<UUID> = mutableSetOf(),
) {
    var ferdig = ferdig
        private set

    fun løst() = behov == løsning

    fun leggTilBehov(behovNavn: String) {
        behov.add(behovNavn)
    }

    fun leggTilLøsning(løsningNavn: String) {
        løsning.add(løsningNavn)
        if (løst()) ferdig = LocalDateTime.now()
    }

    fun kobleTilMelding(meldingsreferanseId: UUID) = meldinger.add(meldingsreferanseId)
}
