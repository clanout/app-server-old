SELECT
  a.event_id,
  a.title,
  a.type,
  a.category,
  a.start_timestamp,
  a.end_timestamp,
  a.organizer_id,
  a.finalized,
  a.create_time,
  b.coordinates [0] AS longitude,
  b.coordinates [1] AS latitude,
  b.name            AS location_name,
  b.zone            AS location_zone,
  c.update_time,
  d.rsvp_status,
  e.attendee_count,
  f.inviter_count,
  g.friend_count,
  h.description     AS description,
  i.status          AS status
FROM
  (
    SELECT DISTINCT events_of_friends.event_id
    FROM
      (
        SELECT DISTINCT event_attendees.event_id AS event_id
        FROM
          (
            SELECT user_id2 AS friend_id
            FROM user_relationships
            WHERE user_id1 = ? AND status <> FALSE
            UNION
            SELECT user_id1 AS friend_id
            FROM user_relationships
            WHERE user_id2 = ? AND status <> FALSE
          ) friends,
          event_attendees
        WHERE event_attendees.attendee_id = friends.friend_id
      ) events_of_friends
      INNER JOIN
      (
        SELECT event_info.event_id
        FROM event_info
        WHERE type <> 1
      ) public_events
        ON public_events.event_id = events_of_friends.event_id
    UNION
    SELECT event_id
    FROM event_attendees
    WHERE attendee_id = ?
    UNION
    SELECT event_invitees.event_id
    FROM event_invitees
    WHERE event_invitees.invitee_id = ?
  ) visible
  INNER JOIN
  (
    SELECT *
    FROM event_info
    WHERE end_timestamp > ?
  ) a
    ON visible.event_id = a.event_id
  INNER JOIN event_location b ON visible.event_id = b.event_id AND b.zone = ?
  INNER JOIN
  (
    SELECT
      event_id,
      MAX(update_time) AS update_time
    FROM event_updates
    GROUP BY event_id
  ) c
    ON visible.event_id = c.event_id
  INNER JOIN
  (
    SELECT *
    FROM event_description
  ) h
    ON visible.event_id = h.event_id
  LEFT JOIN
  (
    SELECT *
    FROM event_attendees
    WHERE attendee_id = ?
  ) d
    ON visible.event_id = d.event_id
  LEFT JOIN
  (
    SELECT
      event_id,
      count(*) AS attendee_count
    FROM event_attendees
    WHERE rsvp_status = 'YES'
    GROUP BY event_id
  ) e
    ON visible.event_id = e.event_id
  LEFT JOIN
  (
    SELECT
      event_id,
      count(*) AS inviter_count
    FROM event_invitees
    WHERE invitee_id = ?
    GROUP BY event_id
  ) f
    ON visible.event_id = f.event_id
  LEFT JOIN
  (
    SELECT
      event_id,
      count(*) AS friend_count
    FROM
      (
        SELECT user_id1 AS friend_id
        FROM user_relationships
        WHERE user_id2 = ?
              AND status <> FALSE
        UNION
        SELECT user_id2 AS friend_id
        FROM user_relationships
        WHERE user_id1 = ?
      ) a
      INNER JOIN
      (
        SELECT *
        FROM event_attendees
        WHERE rsvp_status = 'YES'
      ) b
        ON a.friend_id = b.attendee_id
    GROUP BY event_id
  ) g
    ON visible.event_id = g.event_id
  LEFT JOIN
  (
    SELECT *
    FROM event_attendees
    WHERE attendee_id = ?
  ) i
    ON visible.event_id = i.event_id