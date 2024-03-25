CREATE TABLE message
(
    message_id          uuid PRIMARY KEY,
    data                jsonb                                  NOT NULL,
    lest_dato           TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    behandlet_tidspunkt TIMESTAMP WITH TIME ZONE
);

CREATE TABLE melding
(
    meldingsreferanse_id uuid PRIMARY KEY REFERENCES message (message_id),
    type                 TEXT                                   NOT NULL,
    opprettet            TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    event_name           TEXT                                   NOT NULL
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

CREATE TABLE melding_behov
(
    meldingsreferanse_id uuid PRIMARY KEY REFERENCES melding (meldingsreferanse_id),
    behov_id             uuid NOT NULL
);

CREATE TABLE behov_behov
(
    id                   BIGSERIAL PRIMARY KEY,
    meldingsreferanse_id uuid REFERENCES melding (meldingsreferanse_id),
    behov                TEXT NOT NULL
);

CREATE TABLE behov_løser
(
    id                   BIGSERIAL PRIMARY KEY,
    meldingsreferanse_id uuid REFERENCES melding (meldingsreferanse_id),
    løser                TEXT NOT NULL
);
