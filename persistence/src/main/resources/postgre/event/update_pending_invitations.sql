UPDATE user_info
SET phone = ?
WHERE user_id = ?;

INSERT INTO event_invitees
  SELECT
    a.event_id AS event_id,
    ?          AS invitee_id,
    a.user_id  AS inviter_id
  FROM event_phone_invitations a
  WHERE a.phone = ?;

DELETE FROM event_phone_invitations
WHERE phone = ?