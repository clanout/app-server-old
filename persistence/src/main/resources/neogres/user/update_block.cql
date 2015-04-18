MATCH (me:User {id : {1}}), (friend:User)-[:FRIEND]-(me) WHERE friend.id IN {2}
MERGE (me)-[block:HAS_BLOCKED]->(friend)
ON CREATE SET block.value = TRUE
ON MATCH SET block.value = NOT block.value
WITH me
MATCH (me)-[unblocked:HAS_BLOCKED {value:false}]->(:User)
DELETE unblocked