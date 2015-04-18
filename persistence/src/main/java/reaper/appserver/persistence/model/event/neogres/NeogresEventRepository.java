package reaper.appserver.persistence.model.event.neogres;

import reaper.appserver.persistence.core.neogres.AbstractNeogresRepository;
import reaper.appserver.persistence.core.neogres.NeogresQuery;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventDetails;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class NeogresEventRepository extends AbstractNeogresRepository<Event> implements EventRepository
{
    private static final String SQL_CREATE = NeogresQuery.load("event/create.sql");
    private static final String SQL_CREATE_DESCRIPTION = NeogresQuery.load("event/create_description.sql");
    private static final String SQL_CREATE_LOCATION = NeogresQuery.load("event/create_location.sql");
    private static final String CQL_CREATE = NeogresQuery.load("event/create.cql");

    public NeogresEventRepository()
    {
        super(new NeogresEventMapper());
    }

    @Override
    public String create(Event event, String description)
    {
        String eventId = null;
        Connection connection = null;

        try
        {
            connection = getPostgresConnection();
            connection.setAutoCommit(false);


            // Create event entity
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, event.getTitle());
            preparedStatement.setInt(2, event.getType().getCode());
            preparedStatement.setString(3, event.getCategory());
            preparedStatement.setTimestamp(4, Timestamp.from(event.getStartTime().toInstant()));
            preparedStatement.setTimestamp(5, Timestamp.from(event.getEndTime().toInstant()));
            try
            {
                preparedStatement.setInt(6, Integer.parseInt(event.getOrganizerId()));
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid organizer_id");
            }
            preparedStatement.setBoolean(7, event.isFinalized());
            preparedStatement.setString(8, event.getChatId());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next())
            {
                eventId = resultSet.getString(1);
                break;
            }
            resultSet.close();

            if (eventId == null)
            {
                throw new SQLException("eventId null");
            }
            event.setId(eventId);

            if (description == null)
            {
                description = "";
            }

            // Set event description
            preparedStatement = connection.prepareStatement(SQL_CREATE_DESCRIPTION);
            try
            {
                preparedStatement.setObject(1, UUID.fromString(eventId));
            }
            catch (Exception e)
            {
                throw new SQLException("invalid event_id");
            }
            preparedStatement.setString(2, description);

            preparedStatement.executeUpdate();

            // Set event location
            Event.Location location = event.getLocation();
            preparedStatement = connection.prepareStatement(SQL_CREATE_LOCATION);
            try
            {
                preparedStatement.setObject(1, UUID.fromString(eventId));
            }
            catch (Exception e)
            {
                throw new SQLException("invalid event_id");
            }
            if (location.getName() == null)
            {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            else
            {
                preparedStatement.setString(4, location.getName());
            }
            preparedStatement.setString(5, location.getZone());
            if (location.getX() == null || location.getY() == null)
            {
                preparedStatement.setNull(2, Types.FLOAT);
                preparedStatement.setNull(3, Types.FLOAT);
            }
            else
            {
                preparedStatement.setDouble(2, location.getX());
                preparedStatement.setDouble(3, location.getY());
            }

            preparedStatement.executeUpdate();
            preparedStatement.close();

            // Add event to the graph
            Connection neo4jConnection = getNeo4jConnection();
            PreparedStatement neo4jStatement = neo4jConnection.prepareStatement(CQL_CREATE);
            neo4jStatement.setString(2, event.getOrganizerId());
            neo4jStatement.setString(1, eventId);

            neo4jStatement.executeUpdate();
            close(neo4jStatement, neo4jConnection);

            connection.commit();

            return event.getId();
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
                    log.error("Unable to create new event; [" + e1.getMessage() + "]");
                }
            }
            e.printStackTrace();
            log.error("Unable to create new event; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public void update(Event event, User user, String description)
    {

    }

    @Override
    public void remove(Event event, User user)
    {

    }

    @Override
    public List<Event> getVisibleEvents(User user, String zone)
    {
        return null;
    }

    @Override
    public void setRSVP(Event event, User user, Event.RSVP rsvp)
    {

    }

    @Override
    public void createInvitation(Event event, User from, List<String> to)
    {

    }

    @Override
    public EventDetails getDetails(Event event, User user)
    {
        return null;
    }

    @Override
    public List<Event> getArchive(User user)
    {
        return null;
    }

    @Override
    public Event get(String id)
    {
        return null;
    }

    @Override
    public String create(Event entity)
    {
        return null;
    }

    @Override
    public void update(Event entity)
    {

    }

    @Override
    public void remove(Event entity)
    {

    }
}
