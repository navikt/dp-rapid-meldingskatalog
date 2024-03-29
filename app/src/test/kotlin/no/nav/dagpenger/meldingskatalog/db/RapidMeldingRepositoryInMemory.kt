package no.nav.dagpenger.meldingskatalog.db

import no.nav.dagpenger.meldingskatalog.melding.Innholdstype
import no.nav.dagpenger.meldingskatalog.melding.RapidMelding
import java.util.UUID

class RapidMeldingRepositoryInMemory : RapidMeldingRepository {
    private val observers: MutableList<RapidMeldingRepositoryObserver> = mutableListOf()
    private val messages: MutableList<RapidMelding<*>> = mutableListOf()

    override fun <T : Innholdstype> lagreMelding(melding: RapidMelding<T>) {
        messages.add(melding)
        notifyObservers(melding)
    }

    override fun hentMeldinger() = messages.toList()

    override fun hentMelding(meldingsreferanseId: UUID): RapidMelding<Innholdstype> {
        return messages.first { it.konvolutt.meldingsreferanseId == meldingsreferanseId } as RapidMelding<Innholdstype>
    }

    override fun leggTilObserver(observer: RapidMeldingRepositoryObserver) = observers.add(observer)

    private fun <T : Innholdstype> notifyObservers(message: RapidMelding<T>) {
        observers.forEach { it.onMessageAdded(message) }
    }
}
