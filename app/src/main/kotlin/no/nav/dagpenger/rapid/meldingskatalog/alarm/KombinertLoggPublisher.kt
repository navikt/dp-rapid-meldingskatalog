package no.nav.dagpenger.rapid.meldingskatalog.alarm

import no.nav.dagpenger.rapid.meldingskatalog.AlarmPublisher

class KombinertLoggPublisher(vararg alarmer: AlarmPublisher) : AlarmPublisher {
    private val alarmer = alarmer.toSet()
    override fun publishAlarm(alarmTekst: String) {
        alarmer.forEach { it.publishAlarm(alarmTekst) }
    }
}
