MATCH (me:User {id: {1}}),
(me)-[rel:FRIEND]-(friend:User)
WHERE NOT (friend)-[:HAS_BLOCKED]->(me)
RETURN friend.id, friend.name, EXISTS((me)-[:HAS_FAVOURITED]->(friend)) AS is_favourite, EXISTS((me)-[:HAS_BLOCKED]->(friend)) AS is_blocked