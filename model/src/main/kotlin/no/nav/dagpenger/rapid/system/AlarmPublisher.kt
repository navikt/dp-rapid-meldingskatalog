package no.nav.dagpenger.rapid.system

interface AlarmPublisher {
    fun publishAlarm(alarmTekst: String)
}
