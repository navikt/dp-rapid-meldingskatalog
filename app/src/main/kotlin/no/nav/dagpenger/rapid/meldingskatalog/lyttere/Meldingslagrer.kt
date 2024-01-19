package no.nav.dagpenger.rapid.meldingskatalog.lyttere

import no.nav.dagpenger.meldingskatalog.Meldingsinformasjon
import no.nav.dagpenger.meldingskatalog.Meldingslytter
import no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog.MeldingRepository

internal class Meldingslagrer(private val repository: MeldingRepository) : Meldingslytter {
    override fun gjenkjentMelding(melding: Meldingsinformasjon) {
        repository.lagre(melding)
    }

    override fun ukjentMelding(melding: String) {
    }
}
