package no.nav.dagpenger.rapid.meldingskatalog

import mu.KotlinLogging
import no.nav.dagpenger.rapid.meldingskatalog.api.meldingskatalogAPI
import no.nav.dagpenger.rapid.meldingskatalog.db.PostgresDataSourceBuilder.dataSource
import no.nav.dagpenger.rapid.meldingskatalog.db.PostgresDataSourceBuilder.runMigration
import no.nav.dagpenger.rapid.meldingskatalog.lyttere.Meldingslagrer
import no.nav.dagpenger.rapid.meldingskatalog.lyttere.Meldingslogger
import no.nav.dagpenger.rapid.meldingskatalog.lyttere.Meldingsteller
import no.nav.dagpenger.rapid.meldingskatalog.meldingskatalog.MeldingRepositoryPostgres
import no.nav.dagpenger.rapid.meldingskatalog.rapid.JsonMessageBygger
import no.nav.dagpenger.rapid.meldingskatalog.service.MeldingRiver
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

internal class ApplicationBuilder(configuration: Map<String, String>) : RapidsConnection.StatusListener {
    private val repository = MeldingRepositoryPostgres(dataSource)
    private val rapidsConnection: RapidsConnection =
        RapidApplication.Builder(RapidApplication.RapidApplicationConfig.fromEnv(configuration)).apply {
            withKtorModule { meldingskatalogAPI(repository) }
        }.build()
    private val katalog =
        no.nav.dagpenger.meldingskatalog.Meldingskatalog(
            JsonMessageBygger(),
            kjenteMeldinger,
        )

    init {
        rapidsConnection.register(this)
        MeldingRiver(
            rapidsConnection,
            katalog,
            listOf(Meldingslogger(), Meldingsteller(), Meldingslagrer(repository)),
        )
    }

    fun start() {
        rapidsConnection.start()
    }

    override fun onStartup(rapidsConnection: RapidsConnection) {
        runMigration()
        logger.info { "Starter applikasjonen" }
    }

    override fun onShutdown(rapidsConnection: RapidsConnection) {
        logger.info { "Skrur av applikasjonen" }
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
