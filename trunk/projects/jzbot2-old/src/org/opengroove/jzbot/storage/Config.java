package org.opengroove.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Default;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface Config 
{
    @Property
    @Default(stringValue = "org.opengroove.jzbot.protocols.IrcProtocol")
    public String getProtocol();
    
    public void setProtocol(String protocol);
    
    @Property
    public String getPassword();
    
    public void setPassword(String password);
    
    @Property
    public String getNick();
    
    public void setNick(String nick);
    
    @Property
    public String getServer();
    
    public void setServer(String server);
    
    @Property
    public int getPort();
    
    public void setPort(int port);
    
    // @Property
    // @Default(stringValue = "jeval")
    // public String getEvalEngine();
    //    
    // public void setEvalEngine(String evalEngine);
    //    
    // @Property
    // public String getCharset();
    //    
    // public void setCharset(String charset);
}