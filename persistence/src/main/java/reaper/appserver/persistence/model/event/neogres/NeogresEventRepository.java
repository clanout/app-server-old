package reaper.appserver.persistence.model.event.neogres;

import reaper.appserver.persistence.core.RepositoryActionNotAllowed;
import reaper.appserver.persistence.core.neogres.AbstractNeogresRepository;
import reaper.appserver.persistence.core.neogres.NeogresQuery;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventDetails;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NeogresEventRepository extends AbstractNeogresRepository<Event> implements EventRepository
{
    private static final String SQL_CREATE = NeogresQuery.load("event/create.sql");
    private static final String SQL_CREATE_DESCRIPTION = NeogresQuery.load("event/create_description.sql");
    private static final String SQL_CREATE_LOCATION = NeogresQuery.load("event/create_location.sql");
    private static final String CQL_CREATE = NeogresQuery.load("event/create.cql");

    private static final String CQL_READ = NeogresQuery.load("event/read.cql");

    private static final String SQL_UPDATE = NeogresQuery.load("event/update.sql");
    private static final String SQL_UPDATE_DESCRIPTION = NeogresQuery.load("event/update_description.sql");
    private static final String SQL_UPDATE_LOCATION = NeogresQuery.load("event/update_location.sql");
    private static final String CQL_UPDATE_ELIGIBILITY = NeogresQuery.load("event/update_eligibility.cql");

    private static final String SQL_DELETE = NeogresQuery.load("event/delete.sql");
    private static final String CQL_DELETE = NeogresQuery.load("event/delete.cql");

    public NeogresEventRepository()
    {
        super(new NeogresEventMapper());
    }

    @Override
    public Event get(String id, User user)
    {
        return null;
    }

    @Override
    public String create(Event event, String description)
    {
        Connection connection = null;

        try
        {
            String eventId = null;

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
                throw new SQLException("event_id null");
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
                throw new SQLException("Invalid event_id");
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
                throw new SQLException("Invalid event_id");
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

            neo4jStatement.close();
            neo4jConnection.close();

            connection.commit();
            connection.close();

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
                    log.error(e1.getMessage());
                }
            }
            log.error("Unable to create new event; [" + e.getMessage() + "]");
            return null;
        }
    }

    @Override
    public void update(Event event, User user, String description)
    {
        Connection connection = null;

        try
        {
            Connection neo4jConnection = getNeo4jConnection();
            PreparedStatement neo4jStatement = connection.prepareStatement(CQL_UPDATE_ELIGIBILITY);
            ResultSet resultSet = neo4jStatement.executeQuery();
            while (resultSet.next())
            {
                Boolean canUpdate = resultSet.getBoolean("canUpdate");
                if (!canUpdate)
                {
                    throw new SQLException("User (user_id = " + user.getId() + ") cannot update the Event (event_id = " + event.getId() + ")");
                }
            }
            resultSet.close();
            neo4jStatement.close();
            neo4jConnection.close();

            connection = getPostgresConnection();
            connection.setAutoCommit(false);

            // update event entity
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE);
            preparedStatement.setString(1, event.getTitle());
            preparedStatement.setInt(2, event.getType().getCode());
            preparedStatement.setString(3, event.getCategory());
            preparedStatement.setTimestamp(4, Timestamp.from(event.getStartTime().toInstant()));
            preparedStatement.setTimestamp(5, Timestamp.from(event.getEndTime().toInstant()));
            preparedStatement.setBoolean(6, event.isFinalized());
            try
            {
                preparedStatement.setObject(7, UUID.fromString(event.getId()));
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id");
            }

            preparedStatement.executeUpdate();


            if (description == null)
            {
                description = "";
            }

            // Set event description
            preparedStatement = connection.prepareStatement(SQL_UPDATE_DESCRIPTION);
            preparedStatement.setString(1, description);
            try
            {
                preparedStatement.setObject(2, UUID.fromString(event.getId()));
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id");
            }

            preparedStatement.executeUpdate();

            // Set event location
            Event.Location location = event.getLocation();
            preparedStatement = connection.prepareStatement(SQL_UPDATE_LOCATION);
            if (location.getX() == null || location.getY() == null)
            {
                preparedStatement.setNull(1, Types.FLOAT);
                preparedStatement.setNull(2, Types.FLOAT);
            }
            else
            {
                preparedStatement.setDouble(1, location.getX());
                preparedStatement.setDouble(2, location.getY());
            }
            if (location.getName() == null)
            {
                preparedStatement.setNull(3, Types.VARCHAR);
            }
            else
            {
                preparedStatement.setString(3, location.getName());
            }
            preparedStatement.setString(4, location.getZone());
            try
            {
                preparedStatement.setObject(5, UUID.fromString(event.getId()));
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id");
            }

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
            log.error("Unable to update event with event_id = " + event.getId() + "; [" + e.getMessage() + "]");
        }
    }

    @Override
    public void remove(Event event, User user)
    {
        Connection connection = null;

        try
        {
            if (event.getOrganizerId().equals(user.getId()))
            {
                throw new SQLException("User (user_id = " + user.getId() + ") is not the organizer of the Event (event_id = " + event.getId() + ")");
            }

            connection = getPostgresConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE);
            try
            {
                preparedStatement.setObject(1, UUID.fromString(event.getId()));
            }
            catch (Exception e)
            {
                throw new SQLException("Invalid event_id");
            }
            preparedStatement.executeUpdate();
            preparedStatement.close();

            Connection neo4jConnection = getNeo4jConnection();
            preparedStatement = connection.prepareStatement(CQL_DELETE);
            preparedStatement.setString(1, event.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            neo4jConnection.close();

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

                log.error("Unable to delete event with event_id = " + event.getId() + " [" + e.getMessage() + "]");
            }
        }
    }

    @Override
    public List<Event> getVisibleEvents(User user, String zone)
    {
        try
        {
            List<String> eventIds = new ArrayList<>();

            Connection neo4jConnection = getNeo4jConnection();
            PreparedStatement preparedStatement = neo4jConnection.prepareStatement(CQL_READ);
            preparedStatement.setString(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                eventIds.add(resultSet.getString("evenr.id"));
            }
            resultSet.close();
            preparedStatement.close();

            Connection connection = getPostgresConnection();
            return null;
        }
        catch (SQLException e)
        {
            log.error("Cannot fetch visible events for user_id = " + user.getId() + " [" + e.getMessage() + "]");
            return new ArrayList<>();
        }
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
