UPDATE  event_info
SET     title = ?,
        type = ?,
        category = ?,
        start_timestamp = ?,
        end_timestamp = ?,
        is_finalized = ?
WHERE   event_id = ?