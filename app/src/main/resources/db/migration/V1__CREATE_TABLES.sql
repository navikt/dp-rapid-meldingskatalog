CREATE TABLE meldingstyper
(
    id   BIGSERIAL PRIMARY KEY NOT NULL,
    navn TEXT                  NOT NULL UNIQUE,
    type TEXT                  NOT NULL
);

CREATE INDEX ON meldingstyper (navn);

CREATE TABLE meldingslogg
(
    meldingsreferanse_id uuid PRIMARY KEY         NOT NULL,
    meldingstype_id      BIGINT                   NOT NULL REFERENCES meldingstyper (id),
    opprettet            TIMESTAMP WITH TIME ZONE NOT NULL,
    mottatt              TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE systemer
(
    id   BIGSERIAL PRIMARY KEY NOT NULL,
    navn TEXT                  NOT NULL UNIQUE
);

CREATE TABLE behandlingskjeder
(
    behandlingskjede_id BIGSERIAL PRIMARY KEY                                             NOT NULL,
    mottatt             TIMESTAMP WITH TIME ZONE DEFAULT (NOW() AT TIME ZONE 'utc'::TEXT) NOT NULL,
    system_id           BIGINT                                                            NOT NULL REFERENCES systemer (id),
    meldingstype_id     BIGINT                                                            NOT NULL REFERENCES meldingstyper (id),
    meldingslogg_id     uuid                                                              NOT NULL REFERENCES meldingslogg (meldingsreferanse_id),
    indeks              INT                                                               NOT NULL
);