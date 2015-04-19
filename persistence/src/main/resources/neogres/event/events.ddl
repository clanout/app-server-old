CREATE TABLE event_info
(
  event_id        UUID                     NOT NULL PRIMARY KEY,
  title           TEXT                     NOT NULL,
  type            NUMERIC                  NOT NULL,
  category        TEXT                     NOT NULL,
  start_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
  end_timestamp   TIMESTAMP WITH TIME ZONE NOT NULL,
  organizer_id    BIGINT                   NOT NULL REFERENCES user_info (user_id),
  is_finalized    BOOLEAN                  NOT NULL,
  chat_id         TEXT UNIQUE
);

CREATE TABLE event_description
(
  event_id    UUID NOT NULL REFERENCES event_info (event_id) ON UPDATE CASCADE ON DELETE CASCADE,
  description TEXT
);

CREATE TABLE event_location
(
  event_id  UUID NOT NULL REFERENCES event_info (event_id) ON UPDATE CASCADE ON DELETE CASCADE,
  latitude  FLOAT,
  longitude FLOAT,
  name      TEXT,
  zone      TEXT NOT NULL
);

-- CREATE CONSTRAINT ON (event:Event) ASSERT event.id IS UNIQUE