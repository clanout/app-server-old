SELECT
  visible_events_stats.*,
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
  b.city_cell
FROM event_info a, event_location b,
  (SELECT
     visible_events.event_id,
     c.friend_count,
     d.attendee_count,
     e.inviter_count
   FROM
     (SELECT DISTINCT event_attendees.event_id
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
      WHERE event_attendees.rsvp_status = 'YES'
            AND event_attendees.attendee_id = friends.friend_id
      UNION
      SELECT DISTINCT event_invitees.event_id
      FROM event_invitees
      WHERE event_invitees.invitee_id = ?
     ) visible_events
     LEFT OUTER JOIN
     (
       SELECT
         event_attendees.event_id,
         COUNT(*) AS friend_count
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
       WHERE event_attendees.rsvp_status = 'YES'
             AND event_attendees.attendee_id = friends.friend_id
       GROUP BY event_attendees.event_id
     ) c
       ON c.event_id = visible_events.event_id
     LEFT OUTER JOIN
     (
       SELECT
         event_attendees.event_id,
         COUNT(*) AS attendee_count
       FROM event_attendees
       WHERE event_attendees.rsvp_status = 'YES'
       GROUP BY event_attendees.event_id
     ) d
       ON d.event_id = visible_events.event_id
     LEFT OUTER JOIN
     (
       SELECT
         event_invitees.event_id,
         COUNT(*) AS inviter_count
       FROM event_invitees
       WHERE event_invitees.invitee_id = ?
       GROUP BY event_invitees.event_id
     ) e
       ON e.event_id = visible_events.event_id) visible_events_stats
WHERE a.event_id = visible_events_stats.event_id
      AND b.event_id = visible_events_stats.event_id
      AND b.city_cell = ?
      AND a.event_id = b.event_id;