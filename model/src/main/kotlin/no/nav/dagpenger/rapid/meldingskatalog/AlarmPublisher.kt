package no.nav.dagpenger.rapid.meldingskatalog

interface AlarmPublisher {
    fun publishAlarm(alarmTekst: String)
}
