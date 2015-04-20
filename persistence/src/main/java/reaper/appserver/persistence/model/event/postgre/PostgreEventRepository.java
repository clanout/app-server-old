package reaper.appserver.persistence.model.event.postgre;

import reaper.appserver.persistence.core.RepositoryActionNotAllowed;
import reaper.appserver.persistence.core.postgre.AbstractPostgreRepository;
import reaper.appserver.persistence.core.postgre.PostgreQuery;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventDetails;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgreEventRepository extends AbstractPostgreRepository<Event> implements EventRepository
{
    private static final String SQL_CREATE = PostgreQuery.load("event/create.sql");
    private static final String SQL_CREATE_INVITATION = PostgreQuery.load("event/create_invitation.sql");
    private static final String SQL_READ = PostgreQuery.load("event/read.sql");
    private static final String SQL_READ_VISIBLE = PostgreQuery.load("event/read_visible.sql");
    private static final String SQL_READ_DETAILS = PostgreQuery.load("event/read_details.sql");
    private static final String SQL_READ_ARCHIVE = PostgreQuery.load("event/read_archive.sql");
    private static final String SQL_UPDATE = PostgreQuery.load("event/update.sql");
    private static final String SQL_UPDATE_RSVP = PostgreQuery.load("event/update_rsvp.sql");
    private static final String SQL_DELETE = PostgreQuery.load("event/delete.sql");


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

            close(resultSet, preparedStatement, connection);
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
        return null;
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

            close(resultSet, preparedStatement, connection);
        }
        catch (SQLException e)
        {
            log.error("Unable to read visible events for user with id = " + user.getId() + "; [" + e.getMessage() + "]");
        }

        return visibleEventsList;
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
