package reaper.appserver.persistence.model.event;

import reaper.appserver.persistence.core.Repository;
import reaper.appserver.persistence.model.user.User;

import java.util.List;

public interface EventRepository extends Repository<Event>
{
    public String create(Event event, String description);

    public void update(Event event, User user, String description);

    public void remove(Event event, User user);

    public List<Event> getVisibleEvents(User user, String zone);

    public void setRSVP(Event event, User user, Event.RSVP rsvp);

    public void createInvitation(Event event, User from, List<String> to);

    public EventDetails getDetails(Event event, User user);

    public List<Event> getArchive(User user);
}
