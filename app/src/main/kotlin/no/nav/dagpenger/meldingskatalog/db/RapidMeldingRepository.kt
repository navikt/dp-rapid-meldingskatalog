package no.nav.dagpenger.meldingskatalog.db

import no.nav.dagpenger.meldingskatalog.Tjeneste
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype
import no.nav.dagpenger.meldingskatalog.melding.RapidMelding
import java.util.UUID

interface RapidMeldingRepository {
    fun <T : Innholdstype> lagreMelding(melding: RapidMelding<T>)

    fun hentMeldinger(): List<RapidMelding<*>>

    fun hentMelding(meldingsreferanseId: UUID): RapidMelding<*>

    fun leggTilObserver(observer: RapidMeldingRepositoryObserver): Boolean

    fun hentMeldingstyper(): List<Meldingstype>
}

data class Meldingstype(val navn: String, val type: String, val antall: Int, val involverteTjenester: List<Tjeneste>)

interface RapidMeldingRepositoryObserver {
    fun <T : Innholdstype> onMessageAdded(message: RapidMelding<T>)
}
