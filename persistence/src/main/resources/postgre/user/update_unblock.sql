UPDATE user_relationships
SET status = TRUE
WHERE user_id1 = ?
      AND user_id2 = ANY (?)