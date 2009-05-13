package org.opengroove.jzbot.storage;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface Operator
{
    @Property
    public String getAuthenticatedUrl();
    
    public void setAuthenticatedUrl(String hostname);
    
}