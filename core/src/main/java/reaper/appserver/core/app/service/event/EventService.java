package reaper.appserver.core.app.service.event;

import reaper.appserver.core.app.service.chat.ChatService;
import reaper.appserver.core.app.service.notification.NotificationService;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventDetails;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class EventService
{
    private EventRepository eventRepository;
    private ChatService chatService;
    private NotificationService notificationService;

    public EventService()
    {
        eventRepository = RepositoryFactory.create(Event.class);
        chatService = new ChatService();
        notificationService = new NotificationService();
    }

    public Event getEvent(User user, String eventId)
    {
        if (eventId == null || eventId.isEmpty())
        {
            throw new BadRequest("event_id cannot be null/empty");
        }

        return eventRepository.get(eventId, user);
    }

    public List<Event> getEvents(User user, String zone)
    {
        if (zone == null || zone.isEmpty())
        {
            throw new BadRequest("Zone not specified while fetching visible events");
        }

        return eventRepository.getVisibleEvents(user, zone);
    }

    public Event create(User user, String title, String typeStr, String category, String startTimeStr, String endTimeStr,
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
                startTime = OffsetDateTime.parse(startTimeStr).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();
                endTime = OffsetDateTime.parse(endTimeStr).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();
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
                    Double x = Double.parseDouble(locationLongitude);
                    Double y = Double.parseDouble(locationLatitude);

                    location.setLongitude(x);
                    location.setLatitude(y);
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
            event.setIsFinalized(false);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setOrganizerId(user.getId());
            event.setLocation(location);

            String eventId = eventRepository.create(event, description);
            if (eventId == null)
            {
                throw new ServerError("Unable to create event; user_id = " + user.getId());
            }

            try
            {
                chatService.createChatroom(eventId);
            }
            catch (ServerError e)
            {
                eventRepository.remove(event, user);
                throw new ServerError("Unable to create event because chatroom creation failed (" + e.getMessage() + ")");
            }

            eventRepository.setRSVP(event.getId(), user, Event.RSVP.YES);

            notificationService.eventCreated(user, event);

            return event;
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

        Event event = eventRepository.get(eventId, user);
        if (event == null)
        {
            throw new BadRequest("Cannot delete event; invalid event_id");
        }

        eventRepository.remove(event, user);

        notificationService.eventDeleted(user, event);
    }

    public EventDetails getDetails(String eventId, User user)
    {
        if (eventId == null || eventId.isEmpty())
        {
            throw new BadRequest("Cannot fetch event details; invalid event_id");
        }

        EventDetails eventDetails = eventRepository.getDetails(eventId, user);
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

        eventRepository.setRSVP(eventId, user, rsvp);

        Event event = eventRepository.get(eventId, user);
        notificationService.rsvpChanged(user, event, rsvp);
    }

    public void invite(String eventId, User user, List<String> invitedUsers)
    {
        if (eventId == null || eventId.isEmpty())
        {
            throw new BadRequest("Cannot invite; invalid event_id");
        }

        eventRepository.createInvitation(eventId, user, invitedUsers);

        Event event = eventRepository.get(eventId, user);
        notificationService.eventInvitation(user, invitedUsers, event);
    }

    public Event update(String eventId, User user, String isFinalizedStr, String startTimeStr, String endTimeStr,
                        String locationLatitude, String locationLongitude, String locationName, String locationZone, String description)
    {
        List<String> chatUpdates = new ArrayList<>();

        if (eventId == null || eventId.isEmpty())
        {
            throw new BadRequest("Cannot edit event; invalid event_id");
        }

        Event event = eventRepository.get(eventId, user);
        if (event == null)
        {
            throw new BadRequest("Cannot get event from event_id  = " + eventId);
        }

        boolean isAlreadyFinalized = event.isFinalized();

        try
        {
            if (isFinalizedStr != null)
            {
                boolean isFinalized = Boolean.parseBoolean(isFinalizedStr);
                event.setIsFinalized(isFinalized);

                if (isFinalized)
                {
                    chatUpdates.add(user.getFirstname() + " " + user.getLastname() + " has finalized this event");
                }
                else
                {
                    chatUpdates.add(user.getFirstname() + " " + user.getLastname() + " has unlocked this event");
                }
            }

            if (startTimeStr != null && endTimeStr != null)
            {
                if(isAlreadyFinalized)
                {
                    throw new BadRequest("Cannot reschedule a finalized event");
                }

                try
                {
                    OffsetDateTime startTime = OffsetDateTime.parse(startTimeStr).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();
                    OffsetDateTime endTime = OffsetDateTime.parse(endTimeStr).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();

                    event.setStartTime(startTime);
                    event.setEndTime(endTime);

                    chatUpdates.add(user.getFirstname() + " " + user.getLastname() + " has updated the event timings");
                }
                catch (Exception e)
                {
                    throw new BadRequest("Event start/end time invalid");
                }
            }

            if (locationZone != null)
            {
                if(isAlreadyFinalized)
                {
                    throw new BadRequest("Cannot chage the location for a finalized event");
                }

                Event.Location location = new Event.Location();
                location.setName(locationName);
                location.setZone(locationZone);
                try
                {
                    Double x = Double.parseDouble(locationLongitude);
                    Double y = Double.parseDouble(locationLatitude);

                    location.setLongitude(x);
                    location.setLatitude(y);

                    chatUpdates.add(user.getFirstname() + " " + user.getLastname() + " has updated the event location");
                }
                catch (NumberFormatException e)
                {
                    throw new BadRequest("Event Longitude/Latitude have to be numeric");
                }

                event.setLocation(location);
            }

            eventRepository.update(event, user, description);

            for (String chatUpdate : chatUpdates)
            {
                chatService.postMessages(event.getChatId(), chatUpdate);
            }

            event = eventRepository.get(eventId, user);
            notificationService.eventUpdated(user, event);
            return event;
        }
        catch (BadRequest e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServerError(e.getMessage());
        }
    }

    public List<Event> extractNewEventList(List<Event> allEvents, List<String> eventIds)
    {
        List<Event> newEvents = new ArrayList<>();
        int index = 0;
        while (index < allEvents.size())
        {
            Event event = allEvents.get(index);
            if(!eventIds.contains(event.getId()))
            {
                newEvents.add(event);
                allEvents.remove(index);
            }
            else
            {
                index++;
            }
        }
        return newEvents;
    }

    public List<Event> extractUpdatedEventList(List<Event> allEvents, String lastUpdatedStr)
    {
        OffsetDateTime lastUpdated = null;
        try
        {
            lastUpdated = OffsetDateTime.parse(lastUpdatedStr);
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid last_updated timestamp");
        }


        List<Event> updatedEvents = new ArrayList<>();
        for(Event event : allEvents)
        {
            if(event.getUpdateTime().isAfter(lastUpdated))
            {
                updatedEvents.add(event);
            }
        }
        return updatedEvents;
    }

    public List<String> extractDeletedEventList(List<Event> allEvents, List<String> eventIds)
    {
        List<String> allEventIds = new ArrayList<>(allEvents.size());
        List<String> deletedEvents = new ArrayList<>();

        for(Event event: allEvents)
        {
            allEventIds.add(event.getId());
        }

        for(String eventId:eventIds)
        {
            if(!allEventIds.contains(eventId))
            {
                deletedEvents.add(eventId);
            }
        }

        return deletedEvents;
    }

}
