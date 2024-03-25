package no.nav.dagpenger.meldingskatalog.db

import no.nav.dagpenger.meldingskatalog.melding.Melding
import no.nav.dagpenger.meldingskatalog.meldingskatalog.Meldingskatalog
import no.nav.dagpenger.meldingskatalog.systemkatalog.System
import no.nav.helse.rapids_rivers.JsonMessage

interface MessageRepository {
    fun hentMessage(meldingsreferanseId: String): String
}

interface MeldingRepository : MessageRepository {
    fun lagre(
        message: JsonMessage,
        melding: Melding,
    )

    fun hentMeldinger(): List<Melding>

    fun hentMeldingstyper(): List<Meldingskatalog>

    fun hentSystem(): List<System>
}

interface MeldingRepositoryObserver {
    fun nyMelding(melding: Melding)
}
