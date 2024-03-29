package it.pagopa.pn.commons.configs.cache;

import java.io.Serial;
import java.util.LinkedHashMap;
import java.util.Map;

public class LimitedSizeLinkedHashMap<K,V> extends LinkedHashMap<K, V> {
	
	/**
	 * 
	 */
	@Serial
    private static final long serialVersionUID = 5067004124348499174L;


    private final int maxEntries; //240 bytes ogni K 36 + V 36

    public LimitedSizeLinkedHashMap(int maxEntries){
    	super(10, 0.75f, true);
    	this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxEntries;
    }

}
