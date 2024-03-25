export async function GET({ params, request }) {
  const meldingsreferanseId = params.id;
  return await fetch(
    "http://localhost:8080/melding/" + meldingsreferanseId + "/innhold",
  );
}
