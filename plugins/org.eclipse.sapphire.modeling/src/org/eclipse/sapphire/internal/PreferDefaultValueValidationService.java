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

package org.eclipse.sapphire.internal;

import static org.eclipse.sapphire.modeling.util.internal.SapphireCommonUtil.getDefaultValueLabel;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PreferDefaultValue;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PreferDefaultValueValidationService extends ValidationService
{
    private Listener listener;
    
    @Override
    protected void init()
    {
        super.init();
        
        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                broadcast();
            }
        };
        
        context( Property.class ).attach( this.listener );
    }

    @Override
    public Status validate()
    {
        final Value<?> value = context( Value.class );
        
        if( ! value.empty() )
        {
            final String text = value.text();
            final String def = getDefaultValueLabel( value );
            
            if( def != null && ! def.equals( text ) )
            {
                final String message = NLS.bind( Resources.message, value.definition().getLabel( true, CapitalizationType.FIRST_WORD_ONLY, false ), def );
                return Status.createWarningStatus( message );
            }
        }
        
        return Status.createOkStatus();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.listener != null )
        {
            context( Property.class ).detach( this.listener );
        }
    }
    
    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            final ValueProperty property = context.find( ValueProperty.class );
            
            if( property != null )
            {
                return property.hasAnnotation( PreferDefaultValue.class );
            }
            
            return false;
        }
    }

    private static final class Resources extends NLS
    {
        public static String message;
        
        static
        {
            initializeMessages( PreferDefaultValueValidationService.class.getName(), Resources.class );
        }
    }
    
}
