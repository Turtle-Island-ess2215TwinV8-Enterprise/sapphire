package org.eclipse.sapphire.modeling.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListFactory<E>
{
    private E firstElement = null;
    private List<E> list = null;
    private boolean created = false;
    
    public void add( E element )
    {
        if( this.created )
        {
            throw new IllegalStateException();
        }
        
        if( this.list != null )
        {
            this.list.add( element );
        }
        else if( this.firstElement != null )
        {
            this.list = new ArrayList<E>();
            this.list.add( this.firstElement );
            this.list.add( element );
            this.firstElement = null;
        }
        else
        {
            this.firstElement = element;
        }
    }
    
    public List<E> create()
    {
        if( this.created )
        {
            throw new IllegalStateException();
        }
        
        this.created = true;
        
        if( this.list != null )
        {
            return Collections.unmodifiableList( this.list );
        }
        else if( this.firstElement != null )
        {
            return Collections.singletonList( this.firstElement );
        }
        else
        {
            return Collections.emptyList();
        }
    }
    
}
