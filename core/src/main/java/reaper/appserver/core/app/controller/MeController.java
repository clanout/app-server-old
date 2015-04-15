package reaper.appserver.core.app.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.user.UserDetails;

import java.lang.reflect.Type;
import java.util.List;

public class MeController extends BaseController
{
    public MeController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
    }

    public void friendsAction()
    {
        List<UserDetails.Friend> friends = userService.getFriends(activeUser);

        response.set("friends", friends);
    }

    public void blockAction()
    {
        List<String> blockIdList = null;

        try
        {
            String blockIdListJson = request.getData("user_id_list");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            blockIdList = (new Gson()).fromJson(blockIdListJson, type);
            if (blockIdList == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<user_id> for blocking");
        }

        userService.toggleBlock(activeUser, blockIdList);
    }

    public void favouriteAction()
    {
        List<String> favouriteIdList = null;

        try
        {
            String favouriteIdListJson = request.getData("user_id_list");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            favouriteIdList = (new Gson()).fromJson(favouriteIdListJson, type);
            if (favouriteIdList == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<user_id> for favourites");
        }

        userService.toggleFavourite(activeUser, favouriteIdList);
    }

    public void archiveAction()
    {
        List<Event> archive = userService.getArchive(activeUser);

        response.set("archive", archive);
    }
}
