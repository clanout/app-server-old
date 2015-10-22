package reaper.appserver.core.app.controller;

import com.google.gson.reflect.TypeToken;
import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.app.service.event.EventService;
import reaper.appserver.core.app.service.recommendation.Recommendation;
import reaper.appserver.core.app.service.recommendation.RecommendationService;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;
import reaper.appserver.core.framework.util.GsonProvider;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventDetails;

import java.lang.reflect.Type;
import java.util.List;

public class EventController extends BaseController
{
    private EventService eventService;

    public EventController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
        eventService = new EventService();
    }

    public void mainAction()
    {
        String eventId = request.getData("event_id");
        Event event = eventService.getEvent(activeUser, eventId);
        response.set("event", event);
    }

    public void summaryAction()
    {
        String zone = request.getData("zone");
        List<Event> events = eventService.getEvents(activeUser, zone);
        response.set("events", events);
    }

    public void createAction()
    {
        String title = request.getData("title");
        String type = request.getData("type");
        String category = request.getData("category");
        String startTime = request.getData("start_time");
        String endTime = request.getData("end_time");
        String locationLatitude = request.getData("location_latitude");
        String locationLongitude = request.getData("location_longitude");
        String locationName = request.getData("location_name");
        String locationZone = request.getData("location_zone");
        String description = request.getData("description");

        Event event = eventService.create(activeUser, title, type, category, startTime, endTime, locationLatitude,
                locationLongitude, locationName, locationZone, description);

        response.set("event", event);
    }

    public void deleteAction()
    {
        String eventId = request.getData("event_id");

        eventService.delete(eventId, activeUser);
    }

    public void detailsAction()
    {
        String eventId = request.getData("event_id");

        EventDetails eventDetails = eventService.getDetails(eventId, activeUser);

        response.set("event_details", eventDetails);
    }

    public void rsvpAction()
    {
        String eventId = request.getData("event_id");
        String rsvpStatus = request.getData("rsvp_status");

        eventService.setRsvp(eventId, activeUser, rsvpStatus);
    }

    public void inviteAction()
    {
        String eventId = request.getData("event_id");
        List<String> invitedUsers = null;

        try
        {
            String inviteUsersJson = request.getData("user_id_list");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            invitedUsers = GsonProvider.getGson().fromJson(inviteUsersJson, type);
            if (invitedUsers == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<user_id> for inviting");
        }

        eventService.invite(eventId, activeUser, invitedUsers);

    }

    public void editAction()
    {
        String eventId = request.getData("event_id");
        String startTime = request.getData("start_time");
        String endTime = request.getData("end_time");
        String locationLatitude = request.getData("location_latitude");
        String locationLongitude = request.getData("location_longitude");
        String locationName = request.getData("location_name");
        String locationZone = request.getData("location_zone");
        String description = request.getData("description");

        Event event = eventService.update(eventId, activeUser, startTime, endTime,
                locationLatitude, locationLongitude, locationName, locationZone, description);

        response.set("event", event);
    }

    public void recommendationsAction()
    {
        String latitude = request.getData("latitude");
        String longitude = request.getData("longitude");
        String category = request.getData("category");


        RecommendationService recommendationService = new RecommendationService();
        List<Recommendation> recommendations = recommendationService.getRecommendations(latitude, longitude, category);

        response.set("recommendations", recommendations);
    }

    public void updatesAction()
    {
        String zone = request.getData("zone");
        String lastUpdatedStr = request.getData("last_updated");
        List<String> eventIds = null;

        try
        {
            String eventIdsJson = request.getData("event_list");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            eventIds = GsonProvider.getGson().fromJson(eventIdsJson, type);
            if (eventIds == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<event_id> for fetching updates");
        }

        List<Event> allEvents = eventService.getEvents(activeUser, zone);

        List<Event> newEvents = eventService.extractNewEventList(allEvents, eventIds);
        List<Event> updatedEvents = eventService.extractUpdatedEventList(allEvents, lastUpdatedStr);
        List<String> deletedEvents = eventService.extractDeletedEventList(allEvents, eventIds);

        response.set("new_events", newEvents);
        response.set("updated_events", updatedEvents);
        response.set("deleted_events", deletedEvents);
    }

    public void finalizeAction()
    {
        String eventId = request.getData("event_id");
        boolean isFinalized;

        try
        {
            String isFinalizedStr = request.getData("is_finalized");
            isFinalized = Boolean.parseBoolean(isFinalizedStr);
        }
        catch (Exception e)
        {
            throw new BadRequest("unable to parse is_finalized value");
        }

        eventService.finalize(eventId, isFinalized, activeUser);
    }

    public void chatAction()
    {
        String eventId = request.getData("event_id");
        String eventName = request.getData("event_name");

        if (eventId == null || eventId.isEmpty() || eventName == null || eventName.isEmpty())
        {
            throw new BadRequest("event_id/event_name cannot be null/empty");
        }

        eventService.chatUpdate(activeUser, eventId, eventName);
    }

    public void phoneInvitationAction()
    {
        String eventId = request.getData("event_id");
        List<String> invitedUsers = null;

        try
        {
            String inviteUsersJson = request.getData("phone_numbers");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            invitedUsers = GsonProvider.getGson().fromJson(inviteUsersJson, type);
            if (invitedUsers == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<user_id> for inviting");
        }

        eventService.phoneInvitation(eventId, activeUser, invitedUsers);
    }

    public void statusAction()
    {
        String eventId = request.getData("event_id");
        String status = request.getData("status");
        String notifyStr = request.getData("notification");
        boolean notify = false;

        if (eventId == null || eventId.isEmpty() || status == null)
        {
            throw new BadRequest("event_id/status cannot be null");
        }

        try
        {
            if (notifyStr != null)
            {
                notify = Boolean.parseBoolean(notifyStr);
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid notification parameter");
        }

        eventService.setStatus(activeUser, eventId, status, notify);
    }

    public void invitationResponseAction()
    {
        String eventId = request.getData("event_id");
        String message = request.getData("message");

        if (eventId == null || eventId.isEmpty() || message == null || message.isEmpty())
        {
            throw new BadRequest("event_id/message cannot be null/empty");
        }

        eventService.postInvitationResponse(activeUser, eventId, message);
    }

    public void pendingInvitationsAction()
    {
        String phone = request.getData("phone");
        String zone = request.getData("zone");
        eventService.fetchPendingInvitations(activeUser, phone, zone);
    }
}
