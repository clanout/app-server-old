package reaper.appserver.api.util;

import java.time.*;

public class Test
{
    public static void main(String[] args)
    {
        OffsetDateTime d1 = OffsetDateTime.now();
        System.out.println(d1);

        OffsetDateTime d2 = d1.atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();

        System.out.println(d2);
    }
}
