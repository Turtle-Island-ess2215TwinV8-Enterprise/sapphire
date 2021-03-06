/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.el.internal;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.Transient;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.el.FunctionException;
import org.eclipse.sapphire.modeling.util.NLS;

/**
 * Returns the content of a value or a transient. For value properties, the default is taken into account, if applicable.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ContentFunction extends PropertyFunction<Property>
{
    @Override
    public String name()
    {
        return "Content";
    }
    
    @Override
    protected Object evaluate( final Property property )
    {
        if( property instanceof Value )
        {
            return ( (Value<?>) property ).content();
        }
        else if( property instanceof Transient )
        {
            return ( (Transient<?>) property ).content();
        }
        
        final String msg = NLS.bind( Resources.unsupportedTypeMessage, property.getClass().getName() );
        throw new FunctionException( msg );
    }

    @Override
    protected boolean relevant( final PropertyEvent event )
    {
        return ( event instanceof PropertyContentEvent );
    }
    
    private static final class Resources extends NLS
    {
        public static String unsupportedTypeMessage;
        
        static
        {
            initializeMessages( ContentFunction.class.getName(), Resources.class );
        }
    }
    
}
