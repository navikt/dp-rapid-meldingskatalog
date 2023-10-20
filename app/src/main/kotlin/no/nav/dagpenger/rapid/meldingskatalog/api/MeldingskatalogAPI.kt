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
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingstypeDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.SystemDTO

fun Application.meldingskatalogAPI() {
    install(ContentNegotiation) {
        jackson {}
    }

    install(StatusPages) { }

    routing {
        swaggerUI(path = "openapi", swaggerFile = "meldingskatalog-api.yaml")

        get("/meldingstype") {
            val messageTypes = listOf<MeldingstypeDTO>()
            call.respond(messageTypes)
        }

        get("/meldingstype/{meldingstype}") {
            val meldingstype = call.parameters["meldingstype"]
            val messageType = MeldingstypeDTO(meldingstype ?: "", "Behov", 0, emptyList()) // Replace with your data
            call.respond(messageType)
        }

        get("/meldingstype/{meldingstype}/meldinger") {
            val meldingstype = call.parameters["meldingstype"]
            val messageType = MeldingstypeDTO(meldingstype ?: "", "Behov", 0, emptyList()) // Replace with your data
            call.respond(messageType)
        }

        get("/meldingstype/{meldingstype}/schema") {
            val meldingstype = call.parameters["meldingstype"]
            val messageType = MeldingstypeDTO(meldingstype ?: "", "Behov", 0, emptyList()) // Replace with your data
            call.respond(messageType)
        }

        get("/meldingstype/{meldingstype}/mock") {
            val meldingstype = call.parameters["meldingstype"]
            val messageType = MeldingstypeDTO(meldingstype ?: "", "Behov", 0, emptyList()) // Replace with your data
            call.respond(messageType)
        }

        get("/system") {
            val messageType = SystemDTO("system", emptyList())
            call.respond(messageType)
        }
    }
}
