package no.nav.dagpenger.meldingskatalog.behov

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.meldingskatalog.db.Postgres.withMigratedDb
import org.junit.jupiter.api.Test
import java.util.UUID

class BehovRepositoryPostgresTest {
    private val behovRepositoryPostgres = BehovRepositoryPostgres()

    @Test
    fun `lagrer og henter behov`() =
        withMigratedDb {
            val behovId = UUID.randomUUID()
            behovRepositoryPostgres.medBehov(behovId) {
                leggTilBehov("test")
            }

            with(behovRepositoryPostgres.hentBehov(behovId)) {
                this.behovId shouldBe behovId
                this.behov shouldContainExactly setOf("test")
                this.løsning.shouldBeEmpty()
                this.løst() shouldBe false
            }

            behovRepositoryPostgres.medBehov(behovId) {
                leggTilLøsning("test")
            }

            with(behovRepositoryPostgres.hentBehov(behovId)) {
                this.behovId shouldBe behovId
                this.behov shouldContainExactly setOf("test")
                this.løsning shouldContainExactly setOf("test")
                this.løst() shouldBe true
            }
        }
}
