CREATE SCHEMA IF NOT EXISTS event_sourcing;


CREATE TABLE if NOT EXISTS event_sourcing.aggregate (
    id TEXT NOT NULL,
    object JSONB NOT NULL,
    type TEXT NOT NULL,
    PRIMARY KEY (id, type)
);
