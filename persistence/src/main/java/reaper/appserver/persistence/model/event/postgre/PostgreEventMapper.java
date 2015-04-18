package reaper.appserver.persistence.model.event.postgre;

import reaper.appserver.persistence.core.postgre.PostgreEntityMapper;
import reaper.appserver.persistence.model.event.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgreEventMapper implements PostgreEntityMapper<Event>
{
    @Override
    public Event map(ResultSet resultSet) throws SQLException
    {
        Event event = new Event();

        // title | type | category | start_timestamp | end_timestamp | 
        // organizer_id xmpp_group_id | finalized | event_id | coordinates | name | city_cell |
        // event_id | description | friendsattending | attendeecount | invitercount        
        event.setId(resultSet.getString("event_id"));
        event.setTitle(resultSet.getString("title"));
        event.setType(Event.Type.fromCode(resultSet.getInt("type")));
        event.setCategory(resultSet.getString("category"));
        try
        {
            Timestamp timestamp = resultSet.getTimestamp("start_timestamp");
            OffsetDateTime time = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
            user.setStartTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process start_timestamp (timestamp)");
        }

        try
        {
            Timestamp timestamp = resultSet.getTimestamp("end_timestamp");
            OffsetDateTime time = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
            user.setEndTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process end_timestamp (timestamp)");
        }
        event.setOrganizerId(resultSet.getString("organizer_id"));
        event.setChatId(resultSet.getString("xmpp_group_id"));
        event.setFinalized(resultSet.getBoolean("finalized"));
        
        Event.Location location = new Event.Location();        
        location.setX(resultSet.getDouble("longitude"));
        location.setY(resultSet.getDouble("latitude"));
        location.setName(resultSet.getString("name"));
        location.setZone(resultSet.getString("city_cell"));
        event.setLocation(location);

        event.setAttendeeCount(resultSet.getInt("attendeecount"));
        event.setFriendCount(resultSet.getInt("friendCount"));
        event.setInviterCount(resultSet.getInt("inviterCount"));

        return event;
    }
}
