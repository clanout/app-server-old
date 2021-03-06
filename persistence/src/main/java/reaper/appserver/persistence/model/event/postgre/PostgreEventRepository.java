package reaper.appserver.persistence.model.event.postgre;

import org.postgresql.geometric.PGpoint;
import reaper.appserver.persistence.core.RepositoryActionNotAllowed;
import reaper.appserver.persistence.core.postgre.AbstractPostgreRepository;
import reaper.appserver.persistence.core.postgre.PostgreQuery;
import reaper.appserver.persistence.model.event.*;
import reaper.appserver.persistence.model.user.User;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class PostgreEventRepository extends AbstractPostgreRepository<Event> implements EventRepository
{
    private static final String SQL_CREATE = PostgreQuery.load("event/create.sql");
    private static final String SQL_CREATE_EXTRA = PostgreQuery.load("event/create_extra.sql");

    private static final String SQL_READ = PostgreQuery.load("event/read.sql");
    private static final String SQL_READ_VISIBLE = PostgreQuery.load("event/read_visible.sql");
    private static final String SQL_READ_DETAILS = PostgreQuery.load("event/read_details.sql");
    private static final String SQL_READ_DETAILS_DESCRIPTION = PostgreQuery.load("event/read_details_description.sql");
    private static final String SQL_READ_FRIENDS_GOING = PostgreQuery.load("event/read_friends_going.sql");

    private static final String SQL_UPDATE = PostgreQuery.load("event/update.sql");
    private static final String SQL_UPDATE_FINALIZATION = PostgreQuery.load("event/update_finalization.sql");

    private static final String SQL_DELETE = PostgreQuery.load("event/delete.sql");

    private static final String SQL_CREATE_INVITATION = PostgreQuery.load("event/create_invitation.sql");

    private static final String SQL_CREATE_PHONE_INVITATION = PostgreQuery.load("event/create_phone_invitation.sql");
    private static final String SQL_READ_PENDING_INVITATIONS = PostgreQuery.load("event/read_pending_invitations.sql");
    private static final String SQL_UPDATE_PENDING_INVITATIONS = PostgreQuery.load("event/update_pending_invitations.sql");

    private static final String SQL_UPDATE_RSVP = PostgreQuery.load("event/update_rsvp.sql");
    private static final String SQL_DELETE_RSVP = PostgreQuery.load("event/delete_rsvp.sql");
    private static final String SQL_UPDATE_STATUS = PostgreQuery.load("event/update_status.sql");

    private static final String SQL_READ_SUGGESTIONS = PostgreQuery.load("event/read_suggestions.sql");

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
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ);

            preparedStatement.setObject(1, eventId);
            preparedStatement.setObject(2, eventId);
            preparedStatement.setObject(3, eventId);
            preparedStatement.setObject(4, eventId);
            preparedStatement.setObject(5, eventId);
            preparedStatement.setLong(6, userId);
            preparedStatement.setObject(7, eventId);
            preparedStatement.setObject(8, eventId);
            preparedStatement.setLong(9, userId);
            preparedStatement.setLong(10, userId);
            preparedStatement.setLong(11, userId);
            preparedStatement.setObject(12, eventId);
            preparedStatement.setLong(13, userId);
            preparedStatement.setObject(14, eventId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                event = entityMapper.map(resultSet);
                break;
            }
            resultSet.close();
            preparedStatement.close();

            if (event != null)
            {
                preparedStatement = connection.prepareStatement(SQL_READ_FRIENDS_GOING);
                preparedStatement.setArray(1, connection.createArrayOf("uuid", new Object[]{eventId}));
                preparedStatement.setLong(2, userId);
                preparedStatement.setLong(3, userId);
                resultSet = preparedStatement.executeQuery();

                List<String> friends = new ArrayList<>();

                while (resultSet.next())
                {
                    friends.add(resultSet.getString("firstname"));
                    if (friends.size() == 2)
                    {
                        break;
                    }
                }
                event.setFriends(friends);

                resultSet.close();
                preparedStatement.close();

            }
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to read event with id = " + id + "; [" + e.getMessage() + "]");
        }

        return event;
    }

    @Override
    public String create(Event event)
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

            OffsetDateTime creationTime = OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();

            connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, event.getTitle());
            preparedStatement.setInt(2, event.getType().getCode());
            preparedStatement.setString(3, event.getCategory());
            preparedStatement.setTimestamp(4, Timestamp.from(event.getStartTime().atZoneSameInstant(ZoneOffset.UTC).toInstant()));
            preparedStatement.setTimestamp(5, Timestamp.from(event.getEndTime().atZoneSameInstant(ZoneOffset.UTC).toInstant()));
            preparedStatement.setLong(6, organizerId);
            preparedStatement.setBoolean(7, event.isFinalized());
            preparedStatement.setTimestamp(8, Timestamp.from(creationTime.toInstant()));

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

            PGpoint coordinates = new PGpoint(-1000.0, -1000.0);
            if (location.getLatitude() != null && location.getLongitude() != null)
            {
                coordinates = new PGpoint(location.getLongitude(), location.getLatitude());
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
            String description = event.getDescription();
            if (description == null)
            {
                description = "";
            }
            preparedStatement.setString(6, description);

            // Attendee status of organizer
            preparedStatement.setObject(7, eventId);
            preparedStatement.setLong(8, organizerId);

            // Event updates table
            event.setRsvp(Event.RSVP.YES);
            event.setAttendeeCount(1);
            event.setCreateTime(creationTime);
            event.setUpdateTime(creationTime);

            preparedStatement.setObject(9, eventId);
            preparedStatement.setLong(10, organizerId);
            preparedStatement.setTimestamp(11, Timestamp.from(creationTime.toInstant()));
            preparedStatement.setString(12, String.valueOf(EventUpdate.CREATE));
            preparedStatement.setString(13, Event.Serializer.serialize(event));

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
    public void update(Event event, User user)
    {
        Connection connection = null;

        try
        {
            UUID eventId = null;
            try
            {
                eventId = UUID.fromString(event.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id");
            }

            connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE);

            preparedStatement.setInt(1, event.getType().getCode());
            preparedStatement.setString(2, event.getCategory());
            preparedStatement.setTimestamp(3, Timestamp.from(event.getStartTime().atZoneSameInstant(ZoneOffset.UTC).toInstant()));
            preparedStatement.setTimestamp(4, Timestamp.from(event.getEndTime().atZoneSameInstant(ZoneOffset.UTC).toInstant()));
            preparedStatement.setObject(5, eventId);

            // Location
            Event.Location location = event.getLocation();

            if (location.getName() != null)
            {
                preparedStatement.setString(6, location.getName());
            }
            else
            {
                preparedStatement.setNull(6, Types.VARCHAR);
            }

            PGpoint coordinates = new PGpoint(-1000.0, -1000.0);
            if (location.getLatitude() != null && location.getLongitude() != null)
            {
                coordinates = new PGpoint(location.getLongitude(), location.getLatitude());
            }
            preparedStatement.setObject(7, coordinates);

            preparedStatement.setString(8, location.getZone());
            preparedStatement.setObject(9, eventId);

            preparedStatement.setString(10, event.getDescription());
            preparedStatement.setObject(11, eventId);

            preparedStatement.setObject(12, eventId);
            preparedStatement.setLong(13, Long.parseLong(user.getId()));
            preparedStatement.setTimestamp(14, Timestamp.from(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toInstant()));
            preparedStatement.setString(15, String.valueOf(EventUpdate.EDIT));
            preparedStatement.setString(16, Event.Serializer.serialize(event));

            preparedStatement.executeUpdate();
            preparedStatement.close();

            connection.commit();
            connection.close();
        }
        catch (SQLException e)
        {
            if (connection != null)
            {
                try
                {
                    connection.rollback();
                    connection.close();
                }
                catch (SQLException e1)
                {
                    log.error(e1);
                }
            }

            log.error("Unable to update event with event_id = " + event.getId() + " [" + e.getMessage() + "]");

        }
    }

    @Override
    public void remove(Event event, User user)
    {
        Connection connection = null;

        try
        {
            if (!event.getOrganizerId().equals(user.getId()))
            {
                throw new SQLException("User (user_id = " + user.getId() + ") is not the event organizer");
            }

            UUID eventId = null;
            try
            {
                eventId = UUID.fromString(event.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id");
            }

            connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE);

            preparedStatement.setObject(1, eventId);

            preparedStatement.setObject(2, eventId);
            preparedStatement.setLong(3, Long.parseLong(user.getId()));
            preparedStatement.setTimestamp(4, Timestamp.from(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toInstant()));
            preparedStatement.setString(5, String.valueOf(EventUpdate.REMOVE));
            preparedStatement.setString(6, Event.Serializer.serialize(event));

            preparedStatement.executeUpdate();
            preparedStatement.close();

            connection.commit();

            connection.close();
        }
        catch (SQLException e)
        {
            if (connection != null)
            {
                try
                {
                    connection.rollback();
                    connection.close();
                }
                catch (SQLException e1)
                {
                    log.error(e1.getMessage());
                }
            }

            log.error("Unable to delete event with event_id = " + event.getId() + " [" + e.getMessage() + "]");
        }
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
            preparedStatement.setTimestamp(5, Timestamp.from(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toInstant()));
            preparedStatement.setString(6, zone);
            preparedStatement.setLong(7, userId);
            preparedStatement.setLong(8, userId);
            preparedStatement.setLong(9, userId);
            preparedStatement.setLong(10, userId);
            preparedStatement.setLong(11, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                Event event = entityMapper.map(resultSet);
                visibleEventsList.add(event);

            }

            resultSet.close();
            preparedStatement.close();

            List<String> eventIds = visibleEventsList.stream().map(Event::getId).collect(Collectors.toList());
            Map<String, List<String>> friendsGoingMap = new HashMap<>();

            preparedStatement = connection.prepareStatement(SQL_READ_FRIENDS_GOING);
            preparedStatement.setArray(1, connection.createArrayOf("uuid", eventIds.toArray()));
            preparedStatement.setLong(2, userId);
            preparedStatement.setLong(3, userId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                String eventId = resultSet.getString("event_id");
                List<String> fg = friendsGoingMap.get(eventId);
                if (fg == null)
                {
                    fg = new ArrayList<>();
                }

                if (fg.size() < 2)
                {
                    fg.add(resultSet.getString("firstname"));
                }
                friendsGoingMap.put(eventId, fg);
            }
            resultSet.close();
            preparedStatement.close();

            for (int i = 0; i < eventIds.size(); i++)
            {
                List<String> fg = friendsGoingMap.get(eventIds.get(i));
                if (fg == null)
                {
                    fg = new ArrayList<>();
                }
                visibleEventsList.get(i).setFriends(fg);
            }

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

            if (rsvp == Event.RSVP.NO)
            {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_RSVP);

                preparedStatement.setObject(1, eventId);
                preparedStatement.setLong(2, userId);

                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            else
            {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_RSVP);

                preparedStatement.setObject(1, eventId);
                preparedStatement.setLong(2, userId);
                preparedStatement.setObject(3, eventId);
                preparedStatement.setLong(4, userId);
                preparedStatement.setString(5, String.valueOf(rsvp));

                preparedStatement.executeUpdate();
                preparedStatement.close();
            }

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
            eventDetails.setId(id);
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
                    attendee.setStatus(resultSet.getString("status"));
                    attendee.setIsFriend(resultSet.getBoolean("is_friend"));
                    attendee.setIsInviter(resultSet.getBoolean("is_inviter"));

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
    public boolean setFinalizationState(Event event, boolean isFinalized)
    {
        try
        {
            UUID eventId = null;
            try
            {
                eventId = UUID.fromString(event.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id");
            }

            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_FINALIZATION);
            preparedStatement.setBoolean(1, isFinalized);
            preparedStatement.setObject(2, eventId);

            preparedStatement.setObject(3, eventId);
            preparedStatement.setLong(4, Long.parseLong(event.getOrganizerId()));
            preparedStatement.setTimestamp(5, Timestamp.from(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toInstant()));

            if (isFinalized)
            {
                preparedStatement.setString(6, String.valueOf(EventUpdate.FINALIZE));
            }
            else
            {
                preparedStatement.setString(6, String.valueOf(EventUpdate.UNFINALIZED));
            }
            preparedStatement.setString(7, Event.Serializer.serialize(event));

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            return true;
        }
        catch (SQLException e)
        {
            log.error("Unable to set event finalization [" + e.getMessage() + "]");
            return false;
        }
    }

    @Override
    public void createPhoneInvitations(String id, User from, List<String> phoneNumbers)
    {
        try
        {
            UUID eventId = null;
            Long userId = null;
            Timestamp createTimestamp = Timestamp.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant());

            try
            {
                eventId = UUID.fromString(id);
                userId = Long.parseLong(from.getId());
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id/user_id");
            }

            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE_PHONE_INVITATION);

            for (String phoneNumber : phoneNumbers)
            {
                preparedStatement.setObject(1, eventId);
                preparedStatement.setLong(2, userId);
                preparedStatement.setTimestamp(3, createTimestamp);
                preparedStatement.setString(4, phoneNumber);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to create phone invitations from user_id = " + from.getId() + " for event_id = " + id + " [" + e.getMessage() + "]");
        }
    }

    @Override
    public List<String> processPendingInvitations(User user, String phone)
    {
        try
        {
            List<String> eventsInvited = new ArrayList<>();
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
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_PENDING_INVITATIONS);
            preparedStatement.setString(1, phone);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                String eventId = resultSet.getString("event_id");
                eventsInvited.add(eventId);
            }
            resultSet.close();
            preparedStatement.close();

            preparedStatement = connection.prepareStatement(SQL_UPDATE_PENDING_INVITATIONS);
            preparedStatement.setString(1, phone);
            preparedStatement.setLong(2, userId);
            preparedStatement.setLong(3, userId);
            preparedStatement.setString(4, phone);
            preparedStatement.setString(5, phone);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            return eventsInvited;
        }
        catch (SQLException e)
        {
            log.error("Unable to fetch pending invites for user_id = " + user.getId() + " [" + e.getMessage() + "]");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void setStatus(String id, User user, String status)
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
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_STATUS);

            preparedStatement.setString(1, status);
            preparedStatement.setObject(2, eventId);
            preparedStatement.setLong(3, userId);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e)
        {
            log.error("Unable to update status for user_id = " + user.getId() + " for event_id = " + id + " [" + e.getMessage() + "]");
        }
    }

    @Override
    public List<EventSuggestion> getSuggestions()
    {
        try
        {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_READ_SUGGESTIONS);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<EventSuggestion> suggestions = new ArrayList<>();

            while (resultSet.next())
            {
                EventSuggestion suggestion = new EventSuggestion();
                suggestion.setTitle(resultSet.getString("title"));
                suggestion.setCategory(resultSet.getString("category"));

                suggestions.add(suggestion);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return suggestions;
        }
        catch (SQLException e)
        {
            log.error("Unable to fetch event suggestions [" + e.getMessage() + "]");
            return new ArrayList<>();
        }
    }

    @Override
    public Event get(String id)
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
