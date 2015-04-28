package reaper.appserver.persistence.model.event.postgre;

import com.google.gson.Gson;
import org.postgresql.geometric.PGpoint;
import reaper.appserver.persistence.core.RepositoryActionNotAllowed;
import reaper.appserver.persistence.core.postgre.AbstractPostgreRepository;
import reaper.appserver.persistence.core.postgre.PostgreQuery;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventDetails;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.event.EventUpdate;
import reaper.appserver.persistence.model.user.User;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.*;

public class PostgreEventRepository extends AbstractPostgreRepository<Event> implements EventRepository
{
    private static final String SQL_CREATE = PostgreQuery.load("event/create.sql");
    private static final String SQL_CREATE_EXTRA = PostgreQuery.load("event/create_extra.sql");

    private static final String SQL_READ = PostgreQuery.load("event/read.sql");
    private static final String SQL_READ_VISIBLE = PostgreQuery.load("event/read_visible.sql");
    private static final String SQL_READ_DETAILS = PostgreQuery.load("event/read_details.sql");
    private static final String SQL_READ_DETAILS_DESCRIPTION = PostgreQuery.load("event/read_details_description.sql");

    private static final String SQL_CREATE_INVITATION = PostgreQuery.load("event/create_invitation.sql");

    private static final String SQL_UPDATE_RSVP = PostgreQuery.load("event/update_rsvp.sql");

    public PostgreEventRepository()
    {
        super(new PostgreEventMapper());
    }

    @Override
    public Event get(String id, User user)
    {
        Event event = null;

        try
        {
            UUID eventId = null;
            try
            {
                eventId = UUID.fromString(id);
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id");
            }
            Long userId = null;
            try
            {
                userId = Long.parseLong(user.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ);

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setObject(3, eventId);
            preparedStatement.setObject(4, eventId);
            preparedStatement.setObject(5, eventId);
            preparedStatement.setLong(6, userId);
            preparedStatement.setObject(7, eventId);
            preparedStatement.setObject(8, eventId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                event = entityMapper.map(resultSet);
                break;
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to read event with id = " + id + "; [" + e.getMessage() + "]");
        }

        return event;
    }

    @Override
    public String create(Event event, String description)
    {
        Connection connection = null;

        try
        {
            UUID eventId = null;
            Long organizerId = null;
            try
            {
                organizerId = Long.parseLong(event.getOrganizerId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid organizer_id");
            }


            connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, event.getTitle());
            preparedStatement.setInt(2, event.getType().getCode());
            preparedStatement.setString(3, event.getCategory());
            preparedStatement.setTimestamp(4, Timestamp.from(event.getStartTime().toInstant()));
            preparedStatement.setTimestamp(5, Timestamp.from(event.getEndTime().toInstant()));
            preparedStatement.setLong(6, organizerId);
            preparedStatement.setString(7, event.getChatId());
            preparedStatement.setBoolean(8, event.isFinalized());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next())
            {
                event.setId(resultSet.getString(1));
                break;
            }

            resultSet.close();
            preparedStatement.close();

            try
            {
                eventId = UUID.fromString(event.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id (" + event.getId() + ")");
            }

            preparedStatement = connection.prepareStatement(SQL_CREATE_EXTRA);

            // Location
            Event.Location location = event.getLocation();
            preparedStatement.setObject(1, eventId);

            PGpoint coordinates = new PGpoint(-1.0, -1.0);
            if (location.getY() != null && location.getY() != null)
            {
                coordinates = new PGpoint(location.getX(), location.getY());
            }
            preparedStatement.setObject(2, coordinates);

            if (location.getName() != null)
            {
                preparedStatement.setString(3, location.getName());
            }
            else
            {
                preparedStatement.setNull(3, Types.VARCHAR);
            }

            preparedStatement.setString(4, location.getZone());

            // Description
            preparedStatement.setObject(5, eventId);
            if (description == null)
            {
                description = "";
            }
            preparedStatement.setString(6, description);

            // Attendee status of organizer
            preparedStatement.setObject(7, eventId);
            preparedStatement.setLong(8, organizerId);

            // Event updates table
            preparedStatement.setObject(9, eventId);
            preparedStatement.setLong(10, organizerId);
            preparedStatement.setString(11, String.valueOf(EventUpdate.CREATE));
            preparedStatement.setString(12, Event.Serializer.serialize(event));

            preparedStatement.executeUpdate();
            preparedStatement.close();

            connection.commit();
            connection.close();

            return event.getId();
        }
        catch (SQLException e)
        {
            try
            {
                if (connection != null)
                {
                    connection.rollback();
                    connection.close();
                }
            }
            catch (SQLException e1)
            {
                log.error(e1.getMessage());
            }

            log.error("Unable to create new event; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public void update(Event event, User user, String description)
    {
        throw new RepositoryActionNotAllowed();
    }

    @Override
    public void remove(String id, User user)
    {
        throw new RepositoryActionNotAllowed();
    }

    @Override
    public List<Event> getVisibleEvents(User user, String zone)
    {
        List<Event> visibleEventsList = new ArrayList<Event>();
        try
        {
            Long userId = null;
            try
            {
                userId = Long.parseLong(user.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid user_id");
            }

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_VISIBLE);

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setLong(3, userId);
            preparedStatement.setLong(4, userId);
            preparedStatement.setLong(5, userId);
            preparedStatement.setLong(6, userId);
            preparedStatement.setString(7, zone);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                Event event = entityMapper.map(resultSet);
                visibleEventsList.add(event);

            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to read visible events for user with id = " + user.getId() + "; [" + e.getMessage() + "]");
        }

        return visibleEventsList;
    }

    @Override
    public void setRSVP(String id, User user, Event.RSVP rsvp)
    {
        try
        {
            UUID eventId = null;
            Long userId = null;
            try
            {
                eventId = UUID.fromString(id);
                userId = Long.parseLong(user.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id/user_id");
            }

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_RSVP);

            preparedStatement.setString(1, String.valueOf(rsvp));
            preparedStatement.setObject(2, eventId);
            preparedStatement.setLong(3, userId);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to set RSVP for user_id = " + user.getId() + " for event_id = " + id + "; [" + e.getMessage() + "]");
        }
    }

    @Override
    public void createInvitation(String id, User from, List<String> to)
    {
        try
        {
            UUID eventId = null;
            Long userId = null;
            List<Long> friendIds = new ArrayList<>();
            try
            {
                eventId = UUID.fromString(id);
                userId = Long.parseLong(from.getId());
                for (String friendId : to)
                {
                    friendIds.add(Long.parseLong(friendId));
                }
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id/user_id/friend_ids");
            }

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE_INVITATION);

            for (Long friendId : friendIds)
            {
                preparedStatement.setObject(1, eventId);
                preparedStatement.setLong(2, friendId);
                preparedStatement.setLong(3, userId);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to create invitations fro user_id = " + from.getId() + " for event_id = " + id + " [" + e.getMessage() + "]");
        }
    }

    @Override
    public EventDetails getDetails(String id, User user)
    {
        try
        {
            UUID eventId = null;
            Long userId = null;
            try
            {
                eventId = UUID.fromString(id);
                userId = Long.parseLong(user.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id/user_id");
            }

            EventDetails eventDetails = new EventDetails();
            Set<EventDetails.Attendee> attendees = new HashSet<>();
            Set<EventDetails.Invitee> invitees = new HashSet<>();
            eventDetails.setAttendees(attendees);
            eventDetails.setInvitee(invitees);

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_DETAILS_DESCRIPTION);

            preparedStatement.setObject(1, eventId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                eventDetails.setDescription(resultSet.getString("description"));
                break;
            }

            resultSet.close();
            preparedStatement.close();

            preparedStatement = connection.prepareStatement(SQL_READ_DETAILS);

            preparedStatement.setObject(1, eventId);
            preparedStatement.setObject(2, eventId);
            preparedStatement.setLong(3, userId);
            preparedStatement.setLong(4, userId);
            preparedStatement.setLong(5, userId);
            preparedStatement.setObject(6, eventId);
            preparedStatement.setLong(7, userId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                int type = resultSet.getInt("type");
                if (type == 0)
                {
                    EventDetails.Attendee attendee = new EventDetails.Attendee();
                    attendee.setId(resultSet.getString("user_id"));
                    attendee.setName(resultSet.getString("name"));
                    try
                    {
                        attendee.setRsvp(Event.RSVP.valueOf(resultSet.getString("rsvp_status").toUpperCase()));
                    }
                    catch (Exception e)
                    {
                        throw new SQLException("Unable to process rsvp status");
                    }
                    attendee.setFriend(resultSet.getBoolean("is_friend"));
                    attendee.setInviter(resultSet.getBoolean("is_inviter"));

                    attendees.add(attendee);
                }
                else
                {
                    EventDetails.Invitee invitee = new EventDetails.Invitee();
                    invitee.setId(resultSet.getString("user_id"));
                    invitee.setName(resultSet.getString("name"));

                    invitees.add(invitee);
                }
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return eventDetails;
        }
        catch (SQLException e)
        {
            log.error("Unable to read event details for event_id = " + id + "; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public List<Event> getArchive(User user)
    {
        return null;
    }

    @Override
    public Event get(String id)
    {
        throw new RepositoryActionNotAllowed();
    }

    @Override
    public String create(Event entity)
    {
        throw new RepositoryActionNotAllowed();
    }

    @Override
    public void update(Event entity)
    {
        throw new RepositoryActionNotAllowed();
    }

    @Override
    public void remove(Event entity)
    {
        throw new RepositoryActionNotAllowed();
    }
}
