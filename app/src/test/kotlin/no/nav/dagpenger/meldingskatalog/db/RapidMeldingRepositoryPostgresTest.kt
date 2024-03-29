package no.nav.dagpenger.meldingskatalog.db

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.dagpenger.meldingskatalog.db.Postgres.withMigratedDb
import no.nav.dagpenger.meldingskatalog.fixtures.Testdata.behov
import no.nav.dagpenger.meldingskatalog.fixtures.Testdata.hendelse
import org.junit.jupiter.api.Test

class RapidMeldingRepositoryPostgresTest {
    private val repository = RapidMeldingRepositoryPostgres()

    @Test
    fun lagreMelding() =
        withMigratedDb {
            repository.lagreMelding(hendelse)
            repository.lagreMelding(behov)

            repository.hentMeldinger() shouldHaveSize 2

            repository.hentMelding(hendelse.meldingsreferanseId).meldingsreferanseId shouldBe hendelse.meldingsreferanseId
            with(repository.hentMelding(behov.meldingsreferanseId)) {
                meldingsreferanseId shouldBe behov.meldingsreferanseId
                json shouldEqualJson behov.json
                innhold shouldBe behov.innhold
            }
        }

    @Test
    fun hentMeldinger() {
    }

    @Test
    fun hentMelding() {
    }
}
