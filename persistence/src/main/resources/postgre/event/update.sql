UPDATE event_info
SET type          = ?,
  category        = ?,
  finalized       = ?,
  start_timestamp = ?,
  end_timestamp   = ?
WHERE event_id = ?;

UPDATE event_location
SET name      = ?,
  coordinates = ?,
  city_cell   = ?
WHERE event_id = ?;

UPDATE event_description
SET description = ?
WHERE event_id = ?;

INSERT INTO event_updates
VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?);