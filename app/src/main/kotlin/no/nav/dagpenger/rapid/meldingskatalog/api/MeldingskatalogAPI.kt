package no.nav.dagpenger.rapid.meldingskatalog.api

import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingstypeDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.SystemDTO
import no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog.MeldingRepository

internal fun Application.meldingskatalogAPI(repository: MeldingRepository) {
    install(ContentNegotiation) {
        jackson {}
    }

    install(StatusPages) { }

    routing {
        swaggerUI(path = "openapi", swaggerFile = "meldingskatalog-api.yaml")

        get("/meldingstype") {
            val messageTypes = repository.hentMeldingstyper().map {
                MeldingstypeDTO(it.navn, it.type, 0, emptyList())
            }
            call.respond(messageTypes)
        }

        get("/meldingstype/{meldingstype}") {
            val meldingstype = call.parameters["meldingstype"]!!
            val messageType = repository.hentMeldingstype(meldingstype)
            call.respond(messageType)
        }

        get("/meldingstype/{meldingstype}/meldinger") {
            val meldingstype = call.parameters["meldingstype"]!!
            val messageType = repository.hentMeldinger(meldingstype).map {
                MeldingDTO(
                    meldingsreferanseId = it.meldingsreferanseId,
                    type = meldingstype,
                    systemer = emptyList(),
                )
            }
            call.respond(messageType)
        }

        get("/meldingstype/{meldingstype}/schema") {
            val meldingstype = call.parameters["meldingstype"]
            TODO("Not yet implemented")
            val messageType = MeldingstypeDTO(meldingstype ?: "", "Behov", 0, emptyList()) // Replace with your data
            call.respond(messageType)
        }

        get("/meldingstype/{meldingstype}/mock") {
            val meldingstype = call.parameters["meldingstype"]
            TODO("Not yet implemented")
            val messageType = MeldingstypeDTO(meldingstype ?: "", "Behov", 0, emptyList()) // Replace with your data
            call.respond(messageType)
        }

        get("/system") {
            val system = repository.hentSystem().map {
                SystemDTO(
                    it.navn,
                    it.meldingstyper.map { meldingstype ->
                        MeldingstypeDTO(meldingstype.navn, meldingstype.type, meldingstype.antall, emptyList())
                    },
                )
            }
            call.respond(system)
        }
    }
}
