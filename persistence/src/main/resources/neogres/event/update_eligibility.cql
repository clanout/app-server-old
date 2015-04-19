MATCH (user:User {id: {1}}), (event:Event {id: {2}})
RETURN EXISTS((user)-[:RSVP {status:'YES'}]->(event)) AS canUpdate