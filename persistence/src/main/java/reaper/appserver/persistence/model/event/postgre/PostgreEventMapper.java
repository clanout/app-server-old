package reaper.appserver.persistence.model.event.postgre;

import reaper.appserver.persistence.core.postgre.PostgreEntityMapper;
import reaper.appserver.persistence.model.event.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;

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
            event.setStartTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process start_timestamp (timestamp)");
        }

        try
        {
            Timestamp timestamp = resultSet.getTimestamp("end_timestamp");
            OffsetDateTime time = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
            event.setEndTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process end_timestamp (timestamp)");
        }
        event.setOrganizerId(resultSet.getString("organizer_id"));
        event.setChatId(resultSet.getString("xmpp_group_id"));
        event.setFinalized(resultSet.getBoolean("finalized"));

        Event.Location location = new Event.Location();
        location.setLongitude(resultSet.getDouble("longitude"));
        location.setLatitude(resultSet.getDouble("latitude"));
        location.setName(resultSet.getString("location_name"));
        location.setZone(resultSet.getString("location_zone"));
        event.setLocation(location);

        event.setAttendeeCount(resultSet.getInt("attendee_count"));
        event.setFriendCount(resultSet.getInt("friend_count"));
        event.setInviterCount(resultSet.getInt("inviter_count"));

        try
        {
            event.setRsvp(Event.RSVP.valueOf(resultSet.getString("rsvp_status")));
        }
        catch (Exception e)
        {
            event.setRsvp(Event.RSVP.NO);
        }

        try
        {
            Timestamp timestamp = resultSet.getTimestamp("update_time");
            OffsetDateTime time = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
            event.setUpdateTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process update_time (timestamp)");
        }

        return event;
    }
}
