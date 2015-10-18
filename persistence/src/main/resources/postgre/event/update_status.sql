UPDATE event_attendees
SET status = ?
WHERE event_id = ?
      AND attendee_id = ?