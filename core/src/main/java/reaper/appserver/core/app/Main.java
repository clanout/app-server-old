package reaper.appserver.core.app;

import com.google.gson.GsonBuilder;
import reaper.appserver.core.app.service.chat.ChatService;
import reaper.appserver.core.app.service.recommendation.Recommendation;
import reaper.appserver.core.app.service.recommendation.RecommendationService;

import java.util.List;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        ChatService chatService = new ChatService();
        String eventId = "01214060-39ff-4d2e-9444-02f0b1556ad0";

//        chatService.postMessages(eventId, "Dummy Message at " + System.currentTimeMillis());

        chatService.readHistory(eventId);
    }
}
