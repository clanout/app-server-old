package reaper.appserver.core.app.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.app.service.event.EventService;
import reaper.appserver.core.app.service.recommendation.Recommendation;
import reaper.appserver.core.app.service.recommendation.RecommendationService;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;
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

        String eventId = eventService.create(activeUser, title, type, category, startTime, endTime, locationLatitude,
                locationLongitude, locationName, locationZone, description);

        response.set("event_id", eventId);
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

            invitedUsers = (new Gson()).fromJson(inviteUsersJson, type);
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
        String isFinalized = request.getData("is_finalized");
        String startTime = request.getData("start_time");
        String endTime = request.getData("end_time");
        String locationLatitude = request.getData("location_latitude");
        String locationLongitude = request.getData("location_longitude");
        String locationName = request.getData("location_name");
        String locationZone = request.getData("location_zone");
        String description = request.getData("description");

        Event event = eventService.update(eventId, activeUser, isFinalized, startTime, endTime,
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
}
