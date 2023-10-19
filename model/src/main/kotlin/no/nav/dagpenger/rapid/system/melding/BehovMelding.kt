package no.nav.dagpenger.rapid.system.melding

class BehovMelding(override val type: String) : JsonMelding() {
    override fun erMelding(jsonMessage: String): Boolean {
        val message = parser.readTree(jsonMessage)
        return message["@behov"]?.asText() == type
    }
}
