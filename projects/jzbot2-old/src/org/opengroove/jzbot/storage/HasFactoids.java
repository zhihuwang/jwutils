package org.opengroove.jzbot.storage;

import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Search;
import net.sf.opengroove.common.proxystorage.StoredList;

public interface HasFactoids
{
    public StoredList<Factoid> getFactoids();
    
    public Factoid getFactoid(String name);
    
    public Factoid[] getFactpackFactoids(String factpack);
}