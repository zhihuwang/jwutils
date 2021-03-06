package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class LowerFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(arguments.resolveString(0).toLowerCase());
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {lower|<value>} -- Converts the specified value to lower case.";
    }
    
}
