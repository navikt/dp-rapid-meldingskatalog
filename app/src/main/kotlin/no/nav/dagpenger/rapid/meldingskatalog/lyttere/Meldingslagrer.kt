package no.nav.dagpenger.rapid.meldingskatalog.lyttere

import no.nav.dagpenger.rapid.meldingskatalog.Meldingslytter
import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding
import no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog.MeldingRepository
import no.nav.helse.rapids_rivers.MessageProblems

internal class Meldingslagrer(private val repository: MeldingRepository) : Meldingslytter {

    override fun gjenkjentMelding(melding: IdentifisertMelding) {
        repository.lagre(melding)
    }

    override fun ukjentMelding(melding: String, riverErrors: MutableList<Pair<String, MessageProblems>>) {
    }
}
