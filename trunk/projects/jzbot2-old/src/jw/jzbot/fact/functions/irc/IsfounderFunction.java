package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import org.jibble.pircbot.User;

public class IsfounderFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String channel;
        String user;
        if (arguments.length() == 1)
        {
            channel = context.getChannel();
            user = arguments.getString(0);
        }
        else
        {
            channel = arguments.getString(0);
            user = arguments.getString(1);
        }
        User[] users = context.checkedGetConnection().getConnection().getUsers(channel);
        for (User userObject : users)
        {
            System.out.println("Scanning for user " + userObject.getNick());
            if (userObject.getNick().equalsIgnoreCase(user))
            {
                if (userObject.isFounder())
                    sink.write('1');
                else
                    sink.write('0');
                return;
            }
        }
        throw new FactoidException("Isfounder on user \"" + user + "\", channel \""
                + channel + "\": user is not connected, consider using {{ifjoined}} "
                + "to see if the user is joined");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{isfounder||<channel>||<nick>}} -- Exactly the same as {{isop}}, "
                + "but checks to see if the user is a channel founder. On most servers, "
                + "this is mode +q.";
    }
    
}
