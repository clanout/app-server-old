CREATE (event:Event {id: {1}})
WITH event
MATCH (organizer:User {id: {2}})
CREATE (organizer)-[:IS_ORGANIZER]->(event), (organizer)-[:RSVP {status:'YES'}]->(event)