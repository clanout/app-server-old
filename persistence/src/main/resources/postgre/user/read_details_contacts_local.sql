SELECT
  a.user_id,
  (a.firstname || ' ' || a.lastname) AS name
FROM user_info a, user_location b
WHERE a.phone = ANY (?)
AND a.user_id = b.user_id
AND b.zone = ?