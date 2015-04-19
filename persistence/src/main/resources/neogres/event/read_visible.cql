MATCH (me:User {id : {1}})-[:FRIEND]-(friend:User)-[:RSVP {status: 'YES'}]->(event:Event)
WHERE NOT (me)-[:HAS_BLOCKED]-(friend)
RETURN event.id