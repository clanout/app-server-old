(
  SELECT
    0                     AS type,
    attendees.attendee_id AS user_id,
    attendees.name,
    attendees.rsvp_status,
    CASE WHEN friend_list.friend_id IS NOT NULL THEN TRUE
    ELSE FALSE END        AS is_friend,
    CASE WHEN inviters.inviter_id IS NOT NULL THEN TRUE
    ELSE FALSE END        AS is_inviter
  FROM
    (SELECT
       a.attendee_id,
       (b.firstname || ' ' || b.lastname) AS name,
       a.rsvp_status
     FROM event_attendees a, user_info b
     WHERE event_id = ?
           AND a.attendee_id = b.user_id) AS attendees
    LEFT JOIN
    (SELECT inviter_id
     FROM event_invitees
     WHERE event_id = ?
           AND invitee_id = ?) AS inviters
      ON attendees.attendee_id = inviters.inviter_id
    LEFT JOIN
    (
      (
        SELECT user_id1 AS friend_id
        FROM user_relationships
        WHERE user_id2 = ?
              AND status <> FALSE
      )
      UNION ALL
      (
        SELECT user_id2 AS friend_id
        FROM user_relationships
        WHERE user_id1 = ?
      )
    ) friend_list
      ON attendees.attendee_id = friend_list.friend_id
)
UNION ALL
(
  SELECT
    1                                  AS type,
    a.invitee_id                       AS user_id,
    (b.firstname || ' ' || b.lastname) AS name,
    NULL                               AS rsvp_status,
    NULL                               AS is_friend,
    NULL                               AS is_inviter
  FROM event_invitees a, user_info b
  WHERE a.event_id = ?
        AND a.inviter_id = ?
        AND a.invitee_id = b.user_id
)