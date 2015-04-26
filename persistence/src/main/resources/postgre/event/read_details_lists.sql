SELECT
  invitee_list.attendee_id,
  invitee_list.attendee_type,
  invitee_list.rsvp_status,
  CASE WHEN invitee_list.friend_id IS NOT NULL THEN TRUE
  ELSE FALSE
  END AS IsFriend
FROM
  (
    (
      SELECT a.*
      FROM
        (
          (
            SELECT
              invitee_id AS attendee_id,
              'invitee'  AS attendee_type,
              'dummy'    AS rsvp_status
            FROM event_invitees
            WHERE event_invitees.event_id = ?
                  AND event_invitees.inviter_id = ?
          )
          UNION ALL
          (
            SELECT
              inviter_id AS attendee_id,
              'inviter'  AS attendee_type,
              'dummy'    AS rsvp_status
            FROM event_invitees
            WHERE event_invitees.event_id = ?
                  AND event_invitees.invitee_id = ?
          )
          UNION ALL
          (
            SELECT
              attendee_id AS attendee_id,
              'attendee'  AS attendee_type,
              rsvp_status AS rsvp_status
            FROM event_attendees
            WHERE event_attendees.event_id = ?
          )
        ) a) AS attendee_list
      LEFT OUTER JOIN
      (
      SELECT user_id2 AS friend_id
      FROM user_relationships
      WHERE user_id1 = ?
      UNION
      SELECT user_id1 AS friend_id
      FROM user_relationships
      WHERE user_id2 = ?
      ) friends
      ON attendee_list.attendee_id = friends.friend_id
  ) invitee_list