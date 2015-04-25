UPDATE user_relationships
SET user_id1 = ?,
  user_id2   = ?
WHERE user_id1 = ?
      AND user_id2 = ?
      AND status <> FALSE