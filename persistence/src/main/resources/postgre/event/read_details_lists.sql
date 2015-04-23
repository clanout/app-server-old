SELECT
  a.event_id,
  c.*,
  d.*,
  e.*
FROM event_info a,
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
(
  SELECT attendee_id, rsvp_status
  FROM event_attendees
  WHERE event_attendees.event_id = ?    
) attendees,  
(
  SELECT inviter_id
  FROM event_invitees
  WHERE event_invitees.event_id = ?
    AND event_invitees.invitee_id = ?
) my_inviters,
(
  SELECT invitee_id
  FROM event_invitees
  WHERE event_invitees.event_id = ?
    AND event_invitees.inviter_id = ?
) my_invitees
WHERE a.event_id = ?
  AND b.event_id = ?
  AND a.event_id = b.event_id