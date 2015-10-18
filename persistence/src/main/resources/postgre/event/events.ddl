CREATE TABLE event_info
(
  event_id        UUID                     NOT NULL,
  title           TEXT                     NOT NULL,
  type            NUMERIC                  NOT NULL,
  category        TEXT                     NOT NULL,
  start_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
  end_timestamp   TIMESTAMP WITH TIME ZONE NOT NULL,
  organizer_id    BIGINT                   NOT NULL,
  finalized       BOOLEAN,
  create_time     TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT event_info_pkey PRIMARY KEY (event_id),
  CONSTRAINT event_updates_user_id_fkey FOREIGN KEY (organizer_id)
  REFERENCES user_info (user_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE event_description
(
  event_id    UUID NOT NULL,
  description TEXT,
  CONSTRAINT event_description_event_id_fkey FOREIGN KEY (event_id)
  REFERENCES event_info (event_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE event_location
(
  event_id    UUID NOT NULL,
  coordinates POINT,
  name        TEXT,
  zone        TEXT NOT NULL,
  CONSTRAINT event_location_event_id_fkey FOREIGN KEY (event_id)
  REFERENCES event_info (event_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE event_attendees
(
  event_id    UUID,
  attendee_id BIGINT,
  rsvp_status TEXT,
  status      TEXT,
  CONSTRAINT event_attendees_attendee_id_fkey FOREIGN KEY (attendee_id)
  REFERENCES user_info (user_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT event_attendees_event_id_fkey FOREIGN KEY (event_id)
  REFERENCES event_info (event_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE event_invitees
(
  event_id   UUID,
  invitee_id BIGINT,
  inviter_id BIGINT,
  CONSTRAINT event_invitees_event_id_fkey FOREIGN KEY (event_id)
  REFERENCES event_info (event_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT event_invitees_invitee_id_fkey FOREIGN KEY (invitee_id)
  REFERENCES user_info (user_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT event_invitees_inviter_id_fkey FOREIGN KEY (inviter_id)
  REFERENCES user_info (user_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE event_updates
(
  event_id    UUID,
  user_id     BIGINT,
  update_time TIMESTAMP WITH TIME ZONE,
  update_type TEXT,
  message     TEXT
);

CREATE TABLE event_archive
(
  event_id    UUID,
  user_id     BIGINT,
  update_time TIMESTAMP WITH TIME ZONE,
  update_type TEXT,
  message     TEXT
);

CREATE TABLE event_phone_invitations
(
  event_id         UUID REFERENCES event_info (event_id) ON UPDATE CASCADE ON DELETE CASCADE,
  user_id          BIGINT REFERENCES user_info (user_id) ON UPDATE CASCADE ON DELETE CASCADE,
  create_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
  phone            TEXT                     NOT NULL
);