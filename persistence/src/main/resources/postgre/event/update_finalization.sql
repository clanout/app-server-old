UPDATE event_info
SET finalized = ?
WHERE event_id = ?;

INSERT INTO event_updates
VALUES (?, ?, ?, ?, ?);