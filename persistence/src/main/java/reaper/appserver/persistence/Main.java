package reaper.appserver.persistence;

import reaper.appserver.persistence.core.postgre.PostgreQuery;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        System.out.println(PostgreQuery.load("user/read.sql"));
    }
}
