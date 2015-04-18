MATCH (me:User {id: {1}}), (friend:User)
WHERE friend.id IN {2}
MERGE (me)-[:FRIEND]-(friend)