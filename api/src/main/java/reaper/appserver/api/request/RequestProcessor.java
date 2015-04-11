package reaper.appserver.api.request;

public final class RequestProcessor
{
    public static final String DEFAULT_ACTION = "mainAction";

    public static String getController(String uri)
    {
        if (uri == null || uri.isEmpty())
        {
            return null;
        }

        String[] token = uri.split("/");

        return getControllerName(token[0]);
    }

    public static String getAction(String uri)
    {
        if (uri == null || uri.isEmpty())
        {
            return null;
        }

        String[] token = uri.split("/");
        int length = token.length;

        if (length <= 1)
        {
            return DEFAULT_ACTION;
        }
        else
        {
            return getActionName(token[1]);
        }
    }

    private static String getControllerName(String uriBit)
    {
        StringBuilder temp = new StringBuilder();
        temp.append(Character.toUpperCase(uriBit.charAt(0)));
        temp.append(uriBit.substring(1));
        temp.append("Controller");
        return temp.toString();
    }

    private static String getActionName(String uriBit)
    {
        uriBit = uriBit.toLowerCase();
        String[] token = uriBit.split("_");

        StringBuilder action = new StringBuilder();
        boolean check = true;

        for (int i = 0; i < token.length; i++)
        {
            if (token[i].isEmpty() || token[i] == null)
            {
                continue;
            }

            if (check)
            {
                action.append(token[i]);
                check = false;
            }
            else
            {
                action.append(Character.toUpperCase(token[i].charAt(0)));
                action.append(token[i].substring(1));
            }
        }

        action.append("Action");

        return action.toString();
    }
}
