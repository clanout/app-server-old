CREATE TABLE event_info
(
  event_id        UUID                     NOT NULL,
  title           TEXT                     NOT NULL,
  type            NUMERIC                  NOT NULL,
  category        TEXT                     NOT NULL,
  start_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
  end_timestamp   TIMESTAMP WITH TIME ZONE NOT NULL,
  organizer_id    BIGINT                   NOT NULL,
  xmpp_group_id   TEXT,
  finalized       BOOLEAN,
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
  city_cell   TEXT NOT NULL,
  CONSTRAINT event_location_event_id_fkey FOREIGN KEY (event_id)
  REFERENCES event_info (event_id) MATCH SIMPLE
  ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE event_attendees
(
  event_id    UUID,
  attendee_id BIGINT,
  rsvp_status TEXT,
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