package no.nav.dagpenger.rapid.meldingskatalog.alarm

import mu.KotlinLogging
import no.nav.dagpenger.rapid.meldingskatalog.AlarmPublisher

internal class LoggAlarmPublisher : AlarmPublisher {
    private companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun publishAlarm(alarmTekst: String) {
        logger.warn { "Ukjent melding: $alarmTekst" }
    }
}
