package org.opengroove.jzbot.fact.functions;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class WeatherFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        String zipcode = arguments.get(0);
        String prefix = arguments.length() > 1 ? arguments.get(1) : "";
        try
        {
            URL weatherbugUrl = new URL(
                    "http://a7686974884.isapi.wxbug.net/WxDataISAPI/WxDataISAPI.dll?Magic=10991&RegNum=0&ZipCode="
                            + zipcode.replace("&", "")
                            + "&Units=0&Version=7&Fore=0&t=" + Math.random());
            InputStream stream = weatherbugUrl.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StringUtils.copy(stream, baos);
            stream.close();
            String[] tokens = new String(baos.toByteArray()).split("\\|");
            map.put("date", tokens[2]);
            map.put("time", tokens[1]);
            map.put("temp", tokens[3]);
            map.put("winddir", tokens[4]);
            map.put("winddirhuman", windDegreesToReadable(tokens[4]));
            map.put("windspeed", tokens[5]);
            map.put("gustwdir", tokens[6]);
            map.put("gustwdirhuman", windDegreesToReadable(tokens[6]));
            map.put("gustwspeed", tokens[7]);
            map.put("raintoday", tokens[8]);
            map.put("rainrate", tokens[9]);
            String pressure = tokens[10];
            String pressureDir;
            if (pressure.endsWith("f"))
                pressureDir = "f";
            else if (pressure.endsWith("s"))
                pressureDir = "s";
            else
                pressureDir = "r";
            pressure = pressure.substring(0, pressure.length() - 1);
            map.put("pressure", pressure);
            map.put("pressuredir", pressureDir);
            map.put("humid", tokens[11]);
            map.put("hightemp", tokens[12]);
            map.put("lowtemp", tokens[13]);
            map.put("dewpoint", tokens[14]);
            map.put("windchill", tokens[15]);
            map.put("monthlyrain", tokens[16]);
            map.put("yearlyrain", tokens[31]);
            map.put("gusttime", tokens[25]);
            map.put("station", tokens[35]);
            map.put("citystate", tokens[36]);
            URL yahooUrl = new URL(
                    "http://weather.yahooapis.com/forecastrss?p="
                            + zipcode.replace("&", ""));
            stream = yahooUrl.openStream();
            baos = new ByteArrayOutputStream();
            StringUtils.copy(stream, baos);
            stream.close();
            String yahooResult = new String(baos.toByteArray());
            String conditionStart = "yweather:condition  text=\"";
            int conditionsIndex = yahooResult.indexOf(conditionStart);
            String conditions = "";
            System.out.println("result:" + yahooResult + ",idx:"
                    + conditionsIndex);
            if (conditionsIndex != -1)
            {
                int endIndex = yahooResult.indexOf("\"", conditionsIndex
                        + conditionStart.length() + 1);
                conditions = yahooResult.substring(conditionsIndex
                        + conditionStart.length(), endIndex);
            }
            map.put("conditions", conditions);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new FactoidException("Exception while parsing weather data: "
                    + e.getClass().getName() + ": " + e.getMessage(), e);
        }
        for (String s : map.keySet())
        {
            context.getLocalVars().put(s, map.get(s));
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{weather||<zipcode>||<prefix>}} -- Gets information on the current "
                + "weather conditions for <zipcode> by contacting the WeatherBug and Yahoo "
                + "servers. Weather information is then placed in a number of different local "
                + "variables, each prefixed with <prefix> to avoid conflicting variable names. "
                + "<prefix> is optional, and will default to nothing (IE no prefix) if not present.";
    }
    
    private static final String[] WIND_DIRECTIONS = new String[]
    {
            "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW",
            "WSW", "W", "WNW", "NW", "NNW"
    };
    
    private String windDegreesToReadable(String string)
    {
        double windDegrees = Double.parseDouble(string);
        double sliceSize = 360.0 / (WIND_DIRECTIONS.length * 1.0d);
        double halfSlice = sliceSize / 2.0d;
        windDegrees += halfSlice;
        for (int i = 0; i < WIND_DIRECTIONS.length; i++)
        {
            if (windDegrees >= (i * sliceSize)
                    && windDegrees <= ((i + 1) * sliceSize))
                return WIND_DIRECTIONS[i];
            
        }
        return ("*winddirhuman-" + windDegrees + "-" + sliceSize + "-" + halfSlice);
    }
    
}
