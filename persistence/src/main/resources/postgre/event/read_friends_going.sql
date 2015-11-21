SELECT
  a.event_id,
  c.firstname
FROM
  (
    SELECT *
    FROM event_attendees
    WHERE rsvp_status = 'YES'
          AND event_id = ANY (?)
  ) a
  INNER JOIN
  (
    SELECT user_id1 AS friend_id
    FROM user_relationships
    WHERE user_id2 = ?
          AND status <> FALSE
    UNION
    SELECT user_id2 AS friend_id
    FROM user_relationships
    WHERE user_id1 = ?
  ) b
    ON b.friend_id = a.attendee_id
  INNER JOIN user_info c
    ON b.friend_id = c.user_id
ORDER BY a.event_id