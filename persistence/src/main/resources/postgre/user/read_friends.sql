SELECT user_id2 AS friend_id
FROM user_relationships
WHERE user_id1 = ?
UNION
SELECT user_id1 AS friend_id
FROM user_relationships
WHERE user_id2 = ?