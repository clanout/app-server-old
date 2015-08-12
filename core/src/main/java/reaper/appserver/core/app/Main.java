package reaper.appserver.core.app;

import reaper.appserver.core.app.service.chat.ChatService;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        String eventId = "2508b353-67f5-42d4-96c1-0777534792d4";

        ChatService chatService = new ChatService();
        chatService.postMessages(eventId, "fjfjjk");
    }
}
