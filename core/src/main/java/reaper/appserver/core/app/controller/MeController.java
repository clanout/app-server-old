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
        String zone = null;

        try
        {
            zone = request.getData("zone");
        }
        catch (Exception e)
        {
            throw new ServerError("Unable to process /me/friends request");
        }

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
            String blockIdListJson = request.getData("block_ids");
            String unblockIdListJson = request.getData("unblock_ids");
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
}
