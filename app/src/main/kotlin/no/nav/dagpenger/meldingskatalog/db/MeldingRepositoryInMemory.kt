package no.nav.dagpenger.meldingskatalog.db

import no.nav.dagpenger.meldingskatalog.melding.Behov
import no.nav.dagpenger.meldingskatalog.melding.Hendelse
import no.nav.dagpenger.meldingskatalog.melding.Løsning
import no.nav.dagpenger.meldingskatalog.melding.Melding
import no.nav.dagpenger.meldingskatalog.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.meldingskatalog.systemkatalog.System
import no.nav.helse.rapids_rivers.JsonMessage

class MeldingRepositoryInMemory : MeldingRepository {
    private val observers = mutableListOf<MeldingRepositoryObserver>()

    private val meldinger = mutableListOf<Melding>()
    private val messages = mutableMapOf<String, String>()

    private val hendelser = mutableListOf<Hendelse>()
    private val behov = mutableListOf<Behov>()
    private val løsning = mutableListOf<Løsning>()

    override fun lagre(
        message: JsonMessage,
        melding: Melding,
    ) {
        messages[message.id] = message.toJson()
        meldinger.add(melding)
        when (melding) {
            is Hendelse -> hendelser.add(melding)
            is Behov -> behov.add(melding)
            is Løsning -> løsning.add(melding)
        }
        observers.forEach { it.nyMelding(melding) }
    }

    override fun hentMeldinger() = meldinger.toList()

    override fun hentMeldingstyper(): List<Meldingskatalog> {
        TODO("Not yet implemented")
    }

    override fun hentSystem(): List<System> {
        TODO("Not yet implemented")
    }

    override fun hentMessage(meldingsreferanseId: String) = messages.getValue(meldingsreferanseId)
}
