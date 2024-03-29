package no.nav.dagpenger.meldingskatalog.behov

import java.util.UUID

interface BehovRepository {
    fun medBehov(
        behovId: UUID,
        block: Behov.() -> Unit,
    ) {
        val behov = hentBehov(behovId)
        block(behov)
        lagreBehov(behov)
    }

    fun hentBehov(behovId: UUID): Behov

    fun hentBehov(): List<Behov>

    fun lagreBehov(behov: Behov)
}
