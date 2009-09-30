package org.opengroove.jzbot.fact.functions;

import java.util.Arrays;
import java.util.Collections;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class RandomizeFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String regex = arguments.get(0);
        String string = arguments.get(1);
        String delimiter = arguments.get(2);
        String[] tokens = string.split(regex);
        Collections.shuffle(Arrays.asList(tokens));
        return StringUtils.delimited(tokens, delimiter);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{randomize||<regex>||<string>||<delimiter>}} -- Splits "
                + "<string> around the regular expression <regex>, shuffles the resulting "
                + "list of strings, and then evaluates to a <delimiter>-separated list "
                + "of those strings. For example, {{randomize||\\\\\\.||"
                + "first.second.third.fourth||-}} could evaluate to \"third-second-"
                + "first-fourth\" or maybe \"second-first-fourth-third\".";
    }
    
}
