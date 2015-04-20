package reaper.appserver.core.app.service.event;

import com.google.gson.Gson;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventDetails;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public class EventService
{
    private EventRepository eventRepository;

    public EventService()
    {
        eventRepository = RepositoryFactory.create(Event.class);
    }

    public List<Event> getEvents(User user, String zone)
    {
        if (zone == null || zone.isEmpty())
        {
            throw new BadRequest("Zone not specified while fetching visible events");
        }

        zone = zone.toUpperCase();

        return eventRepository.getVisibleEvents(user, zone);
    }

    public String create(User user, String title, String typeStr, String category, String startTimeStr, String endTimeStr,
                         String locationLatitude, String locationLongitude, String locationName, String locationZone, String description)
    {
        if (title == null || title.isEmpty())
        {
            throw new BadRequest("Event title cannot be null/empty");
        }

        if (typeStr == null || typeStr.isEmpty())
        {
            throw new BadRequest("Event type cannot be null/empty");
        }

        if (category == null || category.isEmpty())
        {
            throw new BadRequest("Event category cannot be null/empty");
        }

        if (startTimeStr == null || startTimeStr.isEmpty() || endTimeStr == null || endTimeStr.isEmpty())
        {
            throw new BadRequest("Event start/end time cannot be null/empty");
        }

        if (locationZone == null || locationZone.isEmpty())
        {
            throw new BadRequest("Event zone (location) cannot be null/empty");
        }


        try
        {
            Event.Type type = null;
            try
            {
                type = Event.Type.valueOf(typeStr.toUpperCase());
            }
            catch (Exception e)
            {
                throw new BadRequest("Event type invalid");
            }


            OffsetDateTime startTime = null;
            OffsetDateTime endTime = null;
            try
            {
                startTime = OffsetDateTime.parse(startTimeStr);
                endTime = OffsetDateTime.parse(endTimeStr);
            }
            catch (Exception e)
            {
                throw new BadRequest("Event start/end time invalid");
            }

            Event.Location location = new Event.Location();
            location.setName(locationName);
            location.setZone(locationZone);
            if (locationLatitude != null && locationLongitude != null)
            {
                try
                {
                    Double x = Double.parseDouble(locationLatitude);
                    Double y = Double.parseDouble(locationLongitude);

                    location.setX(x);
                    location.setY(y);
                }
                catch (NumberFormatException e)
                {
                    throw new BadRequest("Event Longitude/Latitude have to be numeric");
                }
            }

            Event event = new Event();
            event.setTitle(title);
            event.setType(type);
            event.setCategory(category);
            event.setFinalized(false);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setOrganizerId(user.getId());
            event.setLocation(location);

            String eventId = eventRepository.create(event, description);
            if (eventId == null)
            {
                throw new ServerError("Unable to create event; user_id = " + user.getId());
            }

            eventRepository.setRSVP(event, user, Event.RSVP.YES);

            return eventId;
        }
        catch (Exception e)
        {
            throw new ServerError(e.getMessage());
        }
    }

    public void delete(String eventId, User user)
    {
        if (eventId == null || eventId.isEmpty())
        {
            throw new BadRequest("Cannot delete event; invalid event_id");
        }

        Event event = eventRepository.get(eventId);
        if (event == null)
        {
            throw new BadRequest("Cannot get event from event_id  = " + eventId);
        }

        eventRepository.remove(event, user);
    }

    public EventDetails getDetails(String eventId, User user)
    {
        if (eventId == null || eventId.isEmpty())
        {
            throw new BadRequest("Cannot fetch event details; invalid event_id");
        }

        Event event = eventRepository.get(eventId);
        if (event == null)
        {
            throw new BadRequest("Cannot get event from event_id  = " + eventId);
        }

        EventDetails eventDetails = eventRepository.getDetails(event, user);
        if (eventDetails == null)
        {
            throw new ServerError("Unable to fetch details for event_id = " + eventId);
        }

        return eventDetails;
    }

    public void setRsvp(String eventId, User user, String rsvpStatus)
    {
        if (rsvpStatus == null || rsvpStatus.isEmpty())
        {
            throw new BadRequest("Rsvp status cannot be null/empty");
        }

        Event.RSVP rsvp = Event.RSVP.valueOf(rsvpStatus.toUpperCase());

        if (eventId == null || eventId.isEmpty())
        {
            throw new BadRequest("Cannot set rsvp; invalid event_id");
        }

        Event event = eventRepository.get(eventId);
        if (event == null)
        {
            throw new BadRequest("Cannot get event from event_id  = " + eventId);
        }

        eventRepository.setRSVP(event, user, rsvp);

    }

    public void invite(String eventId, User user, List<String> invitedUsers)
    {
        if (eventId == null || eventId.isEmpty())
        {
            throw new BadRequest("Cannot invite; invalid event_id");
        }

        Event event = eventRepository.get(eventId);
        if (event == null)
        {
            throw new BadRequest("Cannot get event from event_id  = " + eventId);
        }

        eventRepository.createInvitation(event, user, invitedUsers);
    }

    public void update(String eventId, User user, String typeStr, String isFinalizedStr, String startTimeStr, String endTimeStr,
                       String locationLatitude, String locationLongitude, String locationName, String locationZone, String description)
    {
        if (eventId == null || eventId.isEmpty())
        {
            throw new BadRequest("Cannot edit event; invalid event_id");
        }

        Event event = eventRepository.get(eventId);
        if (event == null)
        {
            throw new BadRequest("Cannot get event from event_id  = " + eventId);
        }

        try
        {
            if (typeStr != null)
            {
                try
                {
                    Event.Type type = Event.Type.valueOf(typeStr.toUpperCase());
                    event.setType(type);
                }
                catch (Exception e)
                {
                    throw new BadRequest("Event type invalid");
                }
            }

            if (isFinalizedStr != null)
            {
                boolean isFinalized = Boolean.parseBoolean(isFinalizedStr);
                event.setFinalized(isFinalized);
            }

            if (startTimeStr != null && endTimeStr != null)
            {
                try
                {
                    OffsetDateTime startTime = OffsetDateTime.parse(startTimeStr);
                    OffsetDateTime endTime = OffsetDateTime.parse(endTimeStr);

                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                }
                catch (Exception e)
                {
                    throw new BadRequest("Event start/end time invalid");
                }
            }

            if (locationLatitude != null && locationLongitude != null && locationName != null && locationZone != null)
            {
                Event.Location location = new Event.Location();
                location.setName(locationName);
                location.setZone(locationZone);
                try
                {
                    Double x = Double.parseDouble(locationLatitude);
                    Double y = Double.parseDouble(locationLongitude);

                    location.setX(x);
                    location.setY(y);
                }
                catch (NumberFormatException e)
                {
                    throw new BadRequest("Event Longitude/Latitude have to be numeric");
                }

                event.setLocation(location);
            }

            eventRepository.update(event, user, description);
        }
        catch (Exception e)
        {
            throw new ServerError(e.getMessage());
        }
    }
}
