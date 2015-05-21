SELECT
  user_id,
  (firstname || ' ' || lastname) AS name
FROM user_info
WHERE phone = ANY (?)