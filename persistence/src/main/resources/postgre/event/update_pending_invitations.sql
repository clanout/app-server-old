UPDATE user_info
SET phone = ?
WHERE user_id = ?;

INSERT INTO event_invitees
  SELECT
    b.event_id AS event_id,
    ?          AS invitee_id,
    a.user_id  AS inviter_id
  FROM event_phone_invitations a,
    event_info b
  WHERE a.phone = ?
        AND a.event_id = b.event_id;

DELETE FROM event_phone_invitations
WHERE phone = ?