package no.nav.dagpenger.meldingskatalog.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import no.nav.dagpenger.meldingskatalog.db.MeldingRepositoryInMemory
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger
import no.nav.dagpenger.meldingskatalog.melding.Behov
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingDTO
import org.junit.jupiter.api.Disabled
import kotlin.test.Test

class MeldingskatalogAPITest {
    private val objectMapper =
        jacksonObjectMapper().apply {
            findAndRegisterModules()
        }
    private val message = TestMeldinger.behov
    private val repository =
        MeldingRepositoryInMemory().apply {
            lagre(message, Behov.fraMessage(message))
        }

    @Test
    fun testGetMelding() =
        testApplication {
            application {
                meldingskatalogAPI(repository)
            }
            client.get("/melding").apply {
                status shouldBe HttpStatusCode.OK
                val meldinger =
                    bodyAsText().let {
                        objectMapper.readValue(it, Array<MeldingDTO>::class.java).toList()
                    }
                meldinger shouldNotBe null
                meldinger.shouldHaveSize(1)
            }
        }

    @Test
    fun testGetMeldingInnhold() =
        testApplication {
            application {
                meldingskatalogAPI(repository)
            }
            client.get("/melding/${message.id}/innhold").apply {
                status shouldBe HttpStatusCode.OK
                val innhold = bodyAsText()
                innhold shouldNotBe null
            }
        }

    @Test
    @Disabled
    fun testGetMeldingstype() =
        testApplication {
            application {
                meldingskatalogAPI(repository)
            }
            client.get("/meldingstype").apply {
                TODO("Please write your test here")
            }
        }

    @Test
    @Disabled
    fun testGetMeldingstypeMeldingstype() =
        testApplication {
            application {
                meldingskatalogAPI(repository)
            }
            client.get("/meldingstype/{meldingstype}").apply {
                TODO("Please write your test here")
            }
        }

    @Test
    @Disabled
    fun testGetMeldingstypeMeldingstypeMeldinger() =
        testApplication {
            application {
                meldingskatalogAPI(repository)
            }
            client.get("/meldingstype/{meldingstype}/meldinger").apply {
                TODO("Please write your test here")
            }
        }

    @Test
    @Disabled
    fun testGetMeldingstypeMeldingstypeMock() =
        testApplication {
            application {
                meldingskatalogAPI(repository)
            }
            client.get("/meldingstype/{meldingstype}/mock").apply {
                TODO("Please write your test here")
            }
        }

    @Test
    @Disabled
    fun testGetMeldingstypeMeldingstypeSchema() =
        testApplication {
            application {
                meldingskatalogAPI(repository)
            }
            client.get("/meldingstype/{meldingstype}/schema").apply {
                TODO("Please write your test here")
            }
        }

    @Test
    @Disabled
    fun testGetSystem() =
        testApplication {
            application {
                meldingskatalogAPI(repository)
            }
            client.get("/system").apply {
                TODO("Please write your test here")
            }
        }
}
