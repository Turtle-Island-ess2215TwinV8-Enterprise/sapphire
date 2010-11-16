package org.eclipse.sapphire.modeling;

import java.util.IdentityHashMap;
import java.util.Map;

public final class IdentityCache<K,V>
{
    private Map<K,V> map = new IdentityHashMap<K,V>();
    private Map<K,V> next = null;
    
    public V get( final K key )
    {
        final V value = this.map.get( key );
        
        if( this.next != null && value != null )
        {
            this.next.put( key, value );
        }
        
        return value;
    }
    
    public void put( final K key,
                     final V value )
    {
        if( value == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( this.next != null )
        {
            this.next.put( key, value );
        }
        
        this.map.put( key, value );
    }
    
    public void track()
    {
        this.next = new IdentityHashMap<K,V>();
    }
    
    public void purge()
    {
        this.map = this.next;
        this.next = null;
    }
    
}
