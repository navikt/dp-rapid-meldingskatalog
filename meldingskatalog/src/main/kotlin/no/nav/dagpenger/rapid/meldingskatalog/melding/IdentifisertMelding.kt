package no.nav.dagpenger.rapid.meldingskatalog.melding

sealed class IdentifisertMelding(
    val navn: String,
) {
    class Behov(
        navn: String,
    ) : IdentifisertMelding(navn)

    class Hendelse(
        navn: String,
    ) : IdentifisertMelding(navn)
}
