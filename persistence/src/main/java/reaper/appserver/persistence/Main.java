package reaper.appserver.persistence;

import org.apache.log4j.Logger;
import reaper.appserver.log.LogUtil;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.UUID;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        try
        {
            throw new IllegalArgumentException();
        }catch (ArithmeticException e)
        {
            System.out.println("a");
        }
        catch (Exception e)
        {
            System.out.println("e");
        }
    }
}
