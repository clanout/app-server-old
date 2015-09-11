package reaper.appserver.persistence.model.event;

import reaper.appserver.persistence.core.Repository;
import reaper.appserver.persistence.model.user.User;

import java.util.List;

public interface EventRepository extends Repository<Event>
{
    Event get(String id, User user);

    String create(Event event, String description);

    void update(Event event, User user, String description);

    void remove(Event event, User user);

    List<Event> getVisibleEvents(User user, String zone);

    void setRSVP(String id, User user, Event.RSVP rsvp);

    void createInvitation(String id, User from, List<String> to);

    EventDetails getDetails(String id, User user);

    boolean setFinalizationState(Event event, boolean isFinalized);
}
