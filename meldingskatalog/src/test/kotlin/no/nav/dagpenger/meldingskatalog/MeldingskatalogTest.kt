package no.nav.dagpenger.meldingskatalog

private class JsonSchemaIdentifiserer(private val schema: String) : MeldingIdentifiseringStrategi {
    override fun identifiser(melding: UkjentMelding): Boolean = melding.json == schema
}
