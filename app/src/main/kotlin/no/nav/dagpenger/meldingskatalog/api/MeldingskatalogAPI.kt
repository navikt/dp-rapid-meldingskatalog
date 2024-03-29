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
import mu.KotlinLogging
import no.nav.dagpenger.meldingskatalog.behov.BehovRepository
import no.nav.dagpenger.meldingskatalog.db.RapidMeldingRepository
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.BehovType
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.HendelseType
import no.nav.dagpenger.meldingskatalog.melding.Innholdstype.LøsningType
import no.nav.dagpenger.meldingskatalog.melding.Konvolutt
import no.nav.dagpenger.rapid.meldingskatalog.api.models.BehovDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingSporingDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingTypeDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.MeldingstypeDTO
import no.nav.dagpenger.rapid.meldingskatalog.api.models.SystemDTO
import java.util.UUID

private val logger = KotlinLogging.logger {}
private val sikkerlogg = KotlinLogging.logger("tjenestekall.meldingskatalogAPI")

internal fun Application.meldingskatalogAPI(
    rapidMeldingRepository: RapidMeldingRepository,
    behovRepository: BehovRepository,
) {
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
                rapidMeldingRepository.hentMeldinger().map {
                    MeldingDTO(
                        meldingsreferanseId = it.meldingsreferanseId,
                        eventName = it.eventName,
                        opprettet = it.opprettet,
                        sporing = it.sporing.toSporingDTO(),
                        type =
                            when (it.type) {
                                is HendelseType -> MeldingTypeDTO.Hendelse
                                is BehovType -> MeldingTypeDTO.Behov
                                is LøsningType -> MeldingTypeDTO.Løsning
                            },
                    )
                }

            call.respond(meldinger)
        }
        get("/melding/{meldingsreferanseId}/innhold") {
            val meldingsreferanseId = call.parameters["meldingsreferanseId"]!!.let { UUID.fromString(it) }
            val melding = rapidMeldingRepository.hentMelding(meldingsreferanseId)
            call.respond(melding.json)
        }

        get("/behov") {
            val behov =
                behovRepository.hentBehov().map {
                    BehovDTO(
                        behovId = it.behovId,
                        opprettet = it.opprettet,
                        løst = it.ferdig,
                        behov = it.behov.toList(),
                        løsninger = it.løsning.toList(),
                        meldinger = it.meldinger.toList(),
                    )
                }

            call.respond(behov)
        }
        get("/behov/{behovId}") {
            val behovId = call.parameters["behovId"]!!.let { UUID.fromString(it) }
            val behov =
                behovRepository.hentBehov(behovId).let {
                    BehovDTO(
                        behovId = it.behovId,
                        opprettet = it.opprettet,
                        løst = it.ferdig,
                        behov = it.behov.toList(),
                        løsninger = it.løsning.toList(),
                        meldinger = it.meldinger.toList(),
                    )
                }

            call.respond(behov)
        }
        get("/behov/{behovId}/republiser") {
            val behovId = call.parameters["behovId"]!!.let { UUID.fromString(it) }
            val behov = behovRepository.hentBehov(behovId)
            val meldingsreferanseId = behov.meldinger.first()
            val melding = rapidMeldingRepository.hentMelding(meldingsreferanseId)

            logger.info("Republiserer behov=$behovId meldingsreferanseId=$meldingsreferanseId")
            sikkerlogg.info("Republiserer behov=$behovId, meldingsreferanseId=$meldingsreferanseId, melding=$melding")

            call.respond(melding)
        }

        get("/meldingstype") {
            val messageTypes =
                rapidMeldingRepository.hentMeldingstyper().map {
                    MeldingstypeDTO(
                        it.navn,
                        it.type,
                        it.antall,
                        it.involverteTjenester.map { tjeneste -> SystemDTO(tjeneste.navn, emptyList()) },
                    )
                }
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
