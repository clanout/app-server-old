package reaper.appserver.core.app.controller;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.core.app.service.chat.ChatService;
import reaper.appserver.core.app.service.event.EventService;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.core.postgre.PostgreDatabaseAdapter;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserRepository;

import java.time.OffsetDateTime;

public class Main
{
    public static void main(String[] args) throws Exception
    {

        ChatService chatService = new ChatService();
        chatService.postMessages("cdcc1931-6e78-41b6-b3e0-9d91cc98dda4", "start_time:" + OffsetDateTime.now());
        chatService.postMessages("cdcc1931-6e78-41b6-b3e0-9d91cc98dda4", "location:0");
        chatService.postMessages("cdcc1931-6e78-41b6-b3e0-9d91cc98dda4", "location:Arbor Brewing Company");
//        Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();
//
//        PostgreDatabaseAdapter postgreDatabaseAdapter = new PostgreDatabaseAdapter();
//        postgreDatabaseAdapter.init();
//
//        UserRepository userRepository = RepositoryFactory.create(User.class);
//        EventRepository eventRepository = RepositoryFactory.create(Event.class);
//
//        User gaurav = userRepository.get("976303355745864");
//        User aditya = userRepository.getFromUsername("aytida77@gmail.com");
//
//        EventService eventService = new EventService();
//        System.out.println(gson.toJson(eventService.fetchPendingInvitations(aditya, "+917760747507", "Bengaluru")));
//
//        postgreDatabaseAdapter.close();
    }
}
