package reaper.appserver.persistence.model.event.postgre;

import reaper.appserver.persistence.core.postgre.PostgreEntityMapper;
import reaper.appserver.persistence.model.event.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class PostgreEventMapper implements PostgreEntityMapper<Event>
{
    @Override
    public Event map(ResultSet resultSet) throws SQLException
    {
        Event event = new Event();

        event.setId(resultSet.getString("event_id"));
        event.setTitle(resultSet.getString("title"));
        event.setType(Event.Type.fromCode(resultSet.getInt("type")));
        event.setCategory(resultSet.getString("category"));

        try
        {
            Timestamp timestamp = resultSet.getTimestamp("start_timestamp");
            OffsetDateTime time = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
            event.setStartTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process start_timestamp (timestamp)");
        }

        try
        {
            Timestamp timestamp = resultSet.getTimestamp("end_timestamp");
            OffsetDateTime time = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
            event.setEndTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process end_timestamp (timestamp)");
        }

        event.setOrganizerId(resultSet.getString("organizer_id"));
        event.setIsFinalized(resultSet.getBoolean("finalized"));

        Event.Location location = new Event.Location();
        double latitude = resultSet.getDouble("latitude");
        double longitude = resultSet.getDouble("longitude");

        if (latitude == -1000.0 || longitude == -1000.0)
        {
            location.setLatitude(null);
            location.setLongitude(null);
        }
        else
        {
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }
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
            OffsetDateTime time = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
            event.setUpdateTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process update_time (timestamp)");
        }

        try
        {
            Timestamp timestamp = resultSet.getTimestamp("create_time");
            OffsetDateTime time = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
            event.setCreateTime(time);
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to process create_time (timestamp)");
        }

        event.setDescription(resultSet.getString("description"));

        return event;
    }
}
