package reaper.appserver.core.app.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.user.UserDetails;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class MeController extends BaseController
{
    public MeController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
    }

    public void friendsAction()
    {
        String zone = request.getData("zone");

        if (zone == null)
        {
            Set<UserDetails.Friend> friends = userService.getFriends(activeUser);
            response.set("friends", friends);
        }
        else
        {
            Set<UserDetails.Friend> localFriends = userService.getLocalFriends(activeUser, zone);
            response.set("friends", localFriends);
        }
    }

    public void blockAction()
    {
        List<String> blockIdList = null;
        List<String> unblockIdList = null;

        try
        {
            String blockIdListJson = request.getData("blocked_users");
            String unblockIdListJson = request.getData("unblocked_users");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            blockIdList = (new Gson()).fromJson(blockIdListJson, type);
            unblockIdList = (new Gson()).fromJson(unblockIdListJson, type);

            if (blockIdList == null || unblockIdList == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<user_id> for blocking/unblocking");
        }

        userService.toggleBlock(activeUser, blockIdList, unblockIdList);
    }

    public void favouriteAction()
    {
        List<String> favouriteIdList = null;
        List<String> unfavouriteIdList = null;

        try
        {
            String favouriteIdListJson = request.getData("favourite_ids");
            String unfavouriteIdListJson = request.getData("unfavourite_ids");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            favouriteIdList = (new Gson()).fromJson(favouriteIdListJson, type);
            unfavouriteIdList = (new Gson()).fromJson(unfavouriteIdListJson, type);

            if (favouriteIdList == null || unfavouriteIdList == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<user_id> for favourite/unfavourite");
        }

        userService.toggleFavourite(activeUser, favouriteIdList, unfavouriteIdList);
    }

    public void archiveAction()
    {
        List<Event> archive = userService.getArchive(activeUser);

        response.set("archive", archive);
    }

    public void contactsAction()
    {
        List<String> contacts = null;
        String zone = request.getData("zone");

        try
        {
            String contactsJson = request.getData("contacts");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            contacts = (new Gson()).fromJson(contactsJson, type);

            if (contacts == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<contacts> to fetch registered contacts");
        }

        if (zone == null)
        {
            Set<UserDetails.Friend> registeredContacts = userService.getRegisteredContacts(activeUser, contacts);
            response.set("registered_contacts", registeredContacts);
        }
        else
        {
            Set<UserDetails.Friend> registeredContacts = userService.getLocalRegisteredContacts(activeUser, contacts, zone);
            response.set("registered_contacts", registeredContacts);
        }
    }

    public void phoneAction()
    {
        String phone = request.getData("phone");
        userService.setPhone(activeUser, phone);
    }

    public void locationAction()
    {
        String zone = request.getData("zone");
        userService.setLocation(activeUser, zone);
    }

    public void addFriendsAction()
    {
        List<String> userIds = null;

        try
        {
            String userIdJson = request.getData("friend_list");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            userIds = (new Gson()).fromJson(userIdJson, type);

            if (userIds == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<friend_id> to fetch add friends");
        }

        userService.addFriends(activeUser, userIds);
    }
}
