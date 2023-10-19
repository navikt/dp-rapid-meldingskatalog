package no.nav.dagpenger.rapid.system.melding

class HendelseMelding(override val type: String) : JsonMelding() {
    override fun erMelding(jsonMessage: String): Boolean {
        val message = parser.readTree(jsonMessage)
        return message["@event_name"]?.asText() == type
    }
}