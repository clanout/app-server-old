package reaper.appserver.core.app;

import reaper.appserver.core.app.service.chat.ChatService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        List<Double> a = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);

        List<Double> b = a.stream().map(Math::sqrt).collect(Collectors.toList());

        System.out.println(b);
    }
}
