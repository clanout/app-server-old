package reaper.appserver.core.app.controller;

import com.google.gson.reflect.TypeToken;
import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;
import reaper.appserver.core.framework.util.GsonProvider;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.user.UserDetails;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MeController extends BaseController
{
    public MeController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
    }

    public void mainAction()
    {
        response.set("active_user", activeUser);
    }

    public void friendsAction()
    {
        String zone = request.getData("zone");

        if (zone == null)
        {
            Map<String, Set<UserDetails.Friend>> friends = userService.getFriends(activeUser);
            response.set("local_friends", friends.get("local_friends"));
            response.set("other_friends", friends.get("other_friends"));
        }
        else
        {
            Set<UserDetails.Friend> localFriends = userService.getLocalFriends(activeUser);
            response.set("local_friends", localFriends);
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

            blockIdList = GsonProvider.getGson().fromJson(blockIdListJson, type);
            unblockIdList = GsonProvider.getGson().fromJson(unblockIdListJson, type);

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

            favouriteIdList = GsonProvider.getGson().fromJson(favouriteIdListJson, type);
            unfavouriteIdList = GsonProvider.getGson().fromJson(unfavouriteIdListJson, type);

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

            contacts = GsonProvider.getGson().fromJson(contactsJson, type);

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
        boolean isRelocated = userService.setLocation(activeUser, zone);
        response.set("is_relocated", isRelocated);
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

            userIds = GsonProvider.getGson().fromJson(userIdJson, type);

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
