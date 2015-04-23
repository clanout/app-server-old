SELECT
  a.event_id,
  b.description AS description  
FROM event_info a, event_description b
WHERE a.event_id = ?
  AND b.event_id = ?
  AND a.event_id = b.event_id