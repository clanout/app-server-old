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
  b.name,
  b.city_cell,
  c.*,
  d.*,
  e.*
FROM event_info a, event_location b,
  (
    SELECT COUNT(*) AS friend_count
    FROM
      (
        SELECT
          user_id1 AS user_id,
          user_id2 AS friend_id
        FROM user_relationships
        WHERE user_id1 = ?
        UNION
        SELECT
          user_id2 AS user_id,
          user_id1 AS friend_id
        FROM user_relationships
        WHERE user_id2 = ?
      ) friends,
      event_attendees
    WHERE event_attendees.event_id = ?
          AND event_attendees.rsvp_status = 'YES'
          AND event_attendees.attendee_id = friends.friend_id
  ) c,
  (
    SELECT COUNT(*) AS attendee_count
    FROM event_attendees
    WHERE event_attendees.event_id = ?
          AND event_attendees.rsvp_status = 'YES'
  ) d,
  (
    SELECT COUNT(*) AS inviter_count
    FROM event_invitees
    WHERE event_invitees.event_id = ?
          AND event_invitees.invitee_id = ?
  ) e
WHERE a.event_id = ?
      AND b.event_id = ?
      AND a.event_id = b.event_id