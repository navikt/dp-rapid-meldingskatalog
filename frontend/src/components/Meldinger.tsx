import type { components } from "../openapi/schema";
import { Table } from "@navikt/ds-react";
import useSWR from "swr";

interface Props {
  meldinger: components["schemas"]["Melding"][];
}

const fetcher = (...args) => fetch(args).then((res) => res.json());

function Meldingsinnhold({
  meldingsreferanseId,
}: {
  meldingsreferanseId: string;
}) {
  const data = useSWR(`/api/melding/${meldingsreferanseId}/innhold`, fetcher);
  if (data.isLoading) return <div>Loading...</div>;
  if (data.error) return <div>Loading...</div>;
  return <pre>{JSON.stringify(data.data, null, "  ")}</pre>;
}

export default function Meldinger(props: Props) {
  const { meldinger } = props;

  return (
    <Table size="small">
      <Table.Header>
        <Table.Row>
          <Table.HeaderCell scope="col">Opprettet</Table.HeaderCell>
          <Table.HeaderCell scope="col">MeldingID</Table.HeaderCell>
          <Table.HeaderCell scope="col">Type</Table.HeaderCell>
          <Table.HeaderCell scope="col">Navn</Table.HeaderCell>
          <Table.HeaderCell scope="col">Ations</Table.HeaderCell>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {meldinger.map(
          ({ meldingsreferanseId, type, opprettet, eventName }, i) => {
            return (
              <Table.ExpandableRow
                key={meldingsreferanseId}
                content={
                  <Meldingsinnhold meldingsreferanseId={meldingsreferanseId} />
                }
              >
                <Table.DataCell>{format(new Date(opprettet))}</Table.DataCell>
                <Table.DataCell>{meldingsreferanseId}</Table.DataCell>
                <Table.DataCell>{type}</Table.DataCell>
                <Table.HeaderCell scope="row">{eventName}</Table.HeaderCell>
                <Table.DataCell>Resend</Table.DataCell>
              </Table.ExpandableRow>
            );
          },
        )}
      </Table.Body>
    </Table>
  );
}
const format = (date: Date) => {
  const y = date.getFullYear();
  const m = (date.getMonth() + 1).toString().padStart(2, "0");
  const d = date.getDate().toString().padStart(2, "0");
  return `${d}.${m}.${y}`;
};
