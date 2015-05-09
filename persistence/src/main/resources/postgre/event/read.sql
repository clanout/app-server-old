SELECT
  a.event_id,
  a.title,
  a.type,
  a.category,
  a.start_timestamp,
  a.end_timestamp,
  a.organizer_id,
  a.xmpp_group_id,
  a.finalized,
  b.coordinates [0] AS longitude,
  b.coordinates [1] AS latitude,
  b.name            AS location_name,
  b.city_cell       AS location_zone,
  c.update_time,
  d.rsvp_status,
  e.attendee_count,
  f.inviter_count,
  g.friend_count
FROM
  (
    SELECT *
    FROM event_info
    WHERE event_id = ?
  ) a
  INNER JOIN
  (
    SELECT *
    FROM event_location
    WHERE event_id = ?
  ) b
    ON a.event_id = b.event_id
  LEFT JOIN
  (
    SELECT *
    FROM event_updates
    WHERE event_id = ?
    ORDER BY update_time DESC
    LIMIT 1
  ) c
    ON a.event_id = c.event_id
  INNER JOIN
  (
    SELECT *
    FROM event_attendees
    WHERE event_id = ?
          AND attendee_id = ?
  ) d
    ON a.event_id = d.event_id
  LEFT JOIN
  (
    SELECT
      event_id,
      count(*) AS attendee_count
    FROM event_attendees
    WHERE event_id = ?
          AND rsvp_status = 'YES'
    GROUP BY event_id
  ) e
    ON a.event_id = e.event_id
  LEFT JOIN
  (
    SELECT
      event_id,
      count(*) AS inviter_count
    FROM event_invitees
    WHERE event_id = ?
          AND invitee_id = ?
    GROUP BY event_id
  ) f
    ON a.event_id = f.event_id
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
    WHERE event_id = ?
    GROUP BY event_id
  ) g
    ON a.event_id = g.event_id