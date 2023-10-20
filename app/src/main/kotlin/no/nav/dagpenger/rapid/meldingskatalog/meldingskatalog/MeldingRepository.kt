package no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog

import no.nav.dagpenger.rapid.meldingskatalog.melding.IdentifisertMelding

internal interface MeldingRepository {
    fun lagre(melding: IdentifisertMelding)
}
