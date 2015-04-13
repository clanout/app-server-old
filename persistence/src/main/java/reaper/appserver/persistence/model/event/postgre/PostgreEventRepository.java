package reaper.appserver.persistence.model.event.postgre;

import reaper.appserver.persistence.core.RepositoryActionNotAllowed;
import reaper.appserver.persistence.core.postgre.AbstractPostgreRepository;
import reaper.appserver.persistence.core.postgre.PostgreQuery;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventDetails;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;

import java.util.List;

public class PostgreEventRepository extends AbstractPostgreRepository<Event> implements EventRepository
{
    private static final String SQL_CREATE = PostgreQuery.load("event/create.sql");
    private static final String SQL_CREATE_INVITATION = PostgreQuery.load("event/create_invitation.sql");
    private static final String SQL_READ_VISIBLE = PostgreQuery.load("event/read_visible.sql");
    private static final String SQL_READ_DETAILS = PostgreQuery.load("event/read_details.sql");
    private static final String SQL_READ_ARCHIVE = PostgreQuery.load("event/read_archive.sql");
    private static final String SQL_UPDATE = PostgreQuery.load("event/update.sql");
    private static final String SQL_UPDATE_RSVP = PostgreQuery.load("event/update_rsvp.sql");
    private static final String SQL_DELETE = PostgreQuery.load("event/delete.sql");


    public PostgreEventRepository()
    {
        super(new EventEntityMapper());
    }

    @Override
    public String create(Event event, User user, String description)
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
