CREATE TABLE behov
(
    behov_id  uuid PRIMARY KEY,
    opprettet TIMESTAMP WITH TIME ZONE NOT NULL,
    ferdig    TIMESTAMP WITH TIME ZONE
);

CREATE TABLE behov_behov
(
    id       BIGSERIAL PRIMARY KEY,
    behov_id uuid REFERENCES behov (behov_id),
    behov    TEXT NOT NULL
);

CREATE TABLE behov_løsning
(
    id       BIGSERIAL PRIMARY KEY,
    behov_id uuid REFERENCES behov (behov_id),
    løsning  TEXT NOT NULL
);

CREATE TABLE behov_meldinger
(
    id       BIGSERIAL PRIMARY KEY,
    behov_id uuid REFERENCES behov (behov_id),
    melding  uuid REFERENCES melding (meldingsreferanse_id)
);