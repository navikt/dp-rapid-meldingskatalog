package no.nav.dagpenger.rapid.system.alarm

import no.nav.dagpenger.rapid.system.AlarmPublisher

class KombinertLoggPublisher(vararg alarmer: AlarmPublisher) : AlarmPublisher {
    private val alarmer = alarmer.toSet()
    override fun publishAlarm(alarmTekst: String) {
        alarmer.forEach { it.publishAlarm(alarmTekst) }
    }
}
