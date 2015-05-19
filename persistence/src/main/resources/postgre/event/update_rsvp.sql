DELETE FROM event_attendees
WHERE event_id = ?
      AND attendee_id = ?;

INSERT INTO event_attendees
VALUES (?, ?, ?);
