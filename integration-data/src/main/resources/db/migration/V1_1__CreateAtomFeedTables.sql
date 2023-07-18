CREATE TABLE markers
(
  id                           SERIAL PRIMARY KEY,
  feed_uri_for_last_read_entry CHARACTER VARYING(250) NOT NULL,
  feed_uri                     CHARACTER VARYING(250) NOT NULL,
  last_read_entry_id           CHARACTER VARYING(250) NOT NULL
);

CREATE TABLE failed_events
(
  id              SERIAL PRIMARY KEY,
  feed_uri        CHARACTER VARYING(255),
  failed_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  error_message   CHARACTER VARYING(4000),
  event_id        CHARACTER VARYING(255),
  event_content   CHARACTER VARYING(4000),
  error_hash_code INT,
  title           CHARACTER VARYING(255),
  retries         INT NOT NULL DEFAULT 0,
  tags            CHARACTER VARYING(255)
);