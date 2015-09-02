DELETE FROM event_info
WHERE event_id = ?;

INSERT INTO event_updates
VALUES (?, ?, ?, ?, ?);