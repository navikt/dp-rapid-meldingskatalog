package no.nav.dagpenger.meldingskatalog.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.request.get
import no.nav.dagpenger.meldingskatalog.fixtures.TestMeldinger

class MeldingskatalogAPITest {
    private val objectMapper =
        jacksonObjectMapper().apply {
            findAndRegisterModules()
        }
    private val message = TestMeldinger.behov()
    /*jprivate val messageRepository =
        MessageRepositoryPostgres().apply {
            lagreMessage(message)
        }
    private val repository =
        MeldingRepositoryInMemory().apply {
            // lagre(BehovMelding.fraMessage(message))
        }

    @Test
    fun testGetMelding() =
        testApplication {
            application {
                meldingskatalogAPI(messageRepository, repository)
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
                meldingskatalogAPI(messageRepository, repository)
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
                meldingskatalogAPI(messageRepository, repository)
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
                meldingskatalogAPI(messageRepository, repository)
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
                meldingskatalogAPI(messageRepository, repository)
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
                meldingskatalogAPI(messageRepository, repository)
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
                meldingskatalogAPI(messageRepository, repository)
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
                meldingskatalogAPI(messageRepository, repository)
            }
            client.get("/system").apply {
                TODO("Please write your test here")
            }
        }*/
}
