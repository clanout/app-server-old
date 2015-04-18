CREATE TABLE event_info
(
  event_id uuid NOT NULL PRIMARY KEY ,
  title text NOT NULL,
  type numeric NOT NULL,
  category text NOT NULL,
  start_timestamp timestamp with time zone NOT NULL,
  end_timestamp timestamp with time zone NOT NULL,
  organizer_id bigint NOT NULL REFERENCES user_info(user_id),
  is_finalized boolean NOT NULL,
  chat_id text UNIQUE
);

CREATE TABLE event_description
(
  event_id uuid NOT NULL REFERENCES event_info(event_id),
  description text
);

CREATE TABLE event_location
(
  event_id uuid NOT NULL REFERENCES event_info(event_id),
  latitude float,
  longitude float,
  name text,
  zone text NOT NULL
);

-- CREATE CONSTRAINT ON (event:Event) ASSERT event.id IS UNIQUE