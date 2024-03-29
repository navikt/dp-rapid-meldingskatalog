package no.nav.dagpenger.meldingskatalog.behov

import no.nav.dagpenger.meldingskatalog.db.RapidMeldingRepositoryObserver
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.BehovType
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.HendelseType
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.LøsningType
import no.nav.dagpenger.meldingskatalog.melding.RapidMelding
import java.util.UUID

class BehovSporer(private val repository: BehovRepository) : RapidMeldingRepositoryObserver {
    override fun <T : Innholdstype> onMessageAdded(message: RapidMelding<T>) {
        message.innhold.forEach {
            when (it) {
                is HendelseType -> {}
                is BehovType -> leggTil(it, message.meldingsreferanseId)
                is LøsningType -> leggTil(it, message.meldingsreferanseId)
            }
        }
    }

    private fun leggTil(
        behov: BehovType,
        meldingsreferanseId: UUID,
    ) {
        repository.medBehov(behov.behovId) {
            kobleTilMelding(meldingsreferanseId)
            leggTilBehov(behov.behov)
        }
    }

    private fun leggTil(
        løsning: LøsningType,
        meldingsreferanseId: UUID,
    ) {
        repository.medBehov(løsning.behovId) {
            kobleTilMelding(meldingsreferanseId)
            leggTilLøsning(løsning.løser)
        }
    }
}
