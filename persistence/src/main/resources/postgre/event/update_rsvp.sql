UPDATE event_attendees
SET rsvp_status = ?
WHERE event_id = ?
      AND attendee_id = ?