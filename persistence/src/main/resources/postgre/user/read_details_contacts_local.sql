SELECT
  a.user_id,
  (a.firstname || ' ' || a.lastname) AS name
FROM user_info a, user_location b
WHERE a.phone = ANY (?)
AND b.zone = ?