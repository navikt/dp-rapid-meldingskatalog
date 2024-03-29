package no.nav.dagpenger.meldingskatalog.behov

import java.util.UUID

class BehovRepositoryInMemory : BehovRepository {
    private val behovMap: MutableMap<UUID, Behov> = mutableMapOf()

    override fun hentBehov(behovId: UUID) = behovMap[behovId] ?: Behov(behovId)

    override fun lagreBehov(behov: Behov) {
        TODO("Not yet implemented")
    }

    override fun medBehov(
        behovId: UUID,
        block: Behov.() -> Unit,
    ) {
        val behov = hentBehov(behovId)
        block(behov)
        // Lagre
    }
}
