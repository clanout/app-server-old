UPDATE event_info
SET type          = ?,
  category        = ?,
  start_timestamp = ?,
  end_timestamp   = ?
WHERE event_id = ?;

UPDATE event_location
SET name      = ?,
  coordinates = ?,
  zone        = ?
WHERE event_id = ?;

UPDATE event_description
SET description = ?
WHERE event_id = ?;

INSERT INTO event_updates
VALUES (?, ?, ?, ?, ?);