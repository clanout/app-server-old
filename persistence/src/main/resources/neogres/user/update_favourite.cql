MATCH (me:User {id : {1}}), (friend:User)-[:FRIEND]-(me) WHERE friend.id IN {2}
MERGE (me)-[favourite:HAS_FAVOURITED]->(friend)
ON CREATE SET favourite.value = TRUE
ON MATCH SET favourite.value = NOT favourite.value
WITH me
MATCH (me)-[fav:HAS_FAVOURITED {value:false}]->(:User)
DELETE fav