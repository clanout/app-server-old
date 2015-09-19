package reaper.appserver.core.app.service.chat;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws XMPPException, IOException, SmackException
    {
        String eventId = "528f9bfa-09e2-4b41-9f9d-7e63e5722784";
        ChatService chatService = new ChatService();

        chatService.postMessages(eventId, "Hello, World!");

        chatService.readHistory(eventId);
    }
}
