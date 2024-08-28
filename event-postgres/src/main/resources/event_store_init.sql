CREATE SCHEMA IF NOT EXISTS event_sourcing;


CREATE TABLE if NOT EXISTS event_sourcing.event (
    id TEXT NOT NULL,
    aggregate_id TEXT NOT NULL,
    object JSONB NOT NULL,
    type TEXT NOT NULL,
    aggregate_type TEXT NOT NULL,
    processed BOOLEAN NOT NULL DEFAULT false,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_processed ON event_sourcing.event (processed);
