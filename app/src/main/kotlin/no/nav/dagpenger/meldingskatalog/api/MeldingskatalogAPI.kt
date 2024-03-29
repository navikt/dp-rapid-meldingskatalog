package no.nav.dagpenger.meldingskatalog.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
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
import no.nav.dagpenger.meldingskatalog.db.MeldingRepository
import no.nav.dagpenger.meldingskatalog.melding.Behov
import no.nav.dagpenger.meldingskatalog.melding.Hendelse
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.dagpenger.meldingskatalog.melding.Løsning
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingSporingDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingTypeDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingstypeDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.SystemDTO

internal fun Application.meldingskatalogAPI(repository: MeldingRepository) {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    install(StatusPages) { }

    routing {
        swaggerUI(path = "openapi", swaggerFile = "meldingskatalog-api.yaml")

        get("/melding") {
            val meldinger =
                repository.hentMeldinger().map {
                    MeldingDTO(
                        meldingsreferanseId = it.meldingsreferanseId,
                        eventName = it.eventName,
                        opprettet = it.opprettet,
                        sporing = it.sporing.toSporingDTO(),
                        type =
                            when (it) {
                                is Hendelse -> MeldingTypeDTO.Hendelse
                                is Behov -> MeldingTypeDTO.Behov
                                is Løsning -> MeldingTypeDTO.Løsning
                            },
                    )
                }

            call.respond(meldinger)
        }
        get("/melding/{meldingsreferanseId}/innhold") {
            val meldingsreferanseId = call.parameters["meldingsreferanseId"]!!
            val melding = repository.hentMessage(meldingsreferanseId)
            call.respond(melding)
        }

        get("/meldingstype") {
            val messageTypes = emptyList<MeldingstypeDTO>()
            /*repository.hentMeldingstyper().map {
                MeldingstypeDTO(it.navn, it.type, it.antall, it.system.map(::SystemDTO))
            }*/
            call.respond(messageTypes)
        }

        get("/meldingstype/{meldingstype}") {
            val meldingstype = call.parameters["meldingstype"]!!
            val messageType = MeldingstypeDTO("navn", "type", 0, emptyList())
            /*repository.hentMeldingstype(meldingstype).let {
                MeldingstypeDTO(it.navn, it.type, it.antall, it.system.map(::SystemDTO))
            }*/
            call.respond(messageType)
        }

        get("/meldingstype/{meldingstype}/meldinger") {
            val meldingstype = call.parameters["meldingstype"]!!
            val messageType = emptyList<MeldingDTO>()
            /*repository.hentMeldinger(meldingstype).map {
                MeldingDTO(
                    meldingsreferanseId = it.meldingsreferanseId,
                    type = meldingstype,
                    systemer = emptyList(),
                )
            }*/
            call.respond(messageType)
        }

        get("/meldingstype/{meldingstype}/schema") {
            TODO("Not yet implemented")
        }

        get("/meldingstype/{meldingstype}/mock") {
            TODO("Not yet implemented")
        }

        get("/system") {
            val system = emptyList<SystemDTO>()
            /*repository.hentSystem().map {
                SystemDTO(
                    it.navn,
                    it.meldingstyper.map { meldingstype ->
                        MeldingstypeDTO(meldingstype.navn, meldingstype.type, meldingstype.antall, emptyList())
                    },
                )
            }*/
            call.respond(system)
        }
    }
}

private fun List<Konvolutt.Sporing>.toSporingDTO() =
    map { sporing ->
        MeldingSporingDTO(
            id = sporing.id,
            time = sporing.time,
            service = sporing.service,
            instance = sporing.instance,
            image = sporing.image,
        )
    }
