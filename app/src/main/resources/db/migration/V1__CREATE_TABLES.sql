CREATE TABLE melding
(
    meldingsreferanse_id uuid PRIMARY KEY,
    opprettet            TIMESTAMP WITH TIME ZONE               NOT NULL,
    event_name           TEXT                                   NOT NULL,
    data                 jsonb                                  NOT NULL,
    lest_dato            TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    behandlet_tidspunkt  TIMESTAMP WITH TIME ZONE
);

CREATE TABLE sporing
(
    id                   BIGSERIAL PRIMARY KEY,
    meldingsreferanse_id uuid REFERENCES melding (meldingsreferanse_id),
    time                 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    service              TEXT,
    instance             TEXT,
    image                TEXT
);

CREATE TABLE melding_innhold_hendelse
(
    id                   BIGSERIAL PRIMARY KEY,
    meldingsreferanse_id uuid REFERENCES melding (meldingsreferanse_id),
    navn                 TEXT NOT NULL
);
CREATE TABLE melding_innhold_behov
(
    id                   BIGSERIAL PRIMARY KEY,
    meldingsreferanse_id uuid REFERENCES melding (meldingsreferanse_id),
    behov_id             uuid NOT NULL,
    behov                TEXT NOT NULL
);
CREATE TABLE melding_innhold_løsning
(
    id                   BIGSERIAL PRIMARY KEY,
    meldingsreferanse_id uuid REFERENCES melding (meldingsreferanse_id),
    behov_id             uuid NOT NULL,
    løsning              TEXT NOT NULL
);