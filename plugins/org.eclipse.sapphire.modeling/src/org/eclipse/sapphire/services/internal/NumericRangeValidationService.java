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

package org.eclipse.sapphire.services.internal;

import org.eclipse.sapphire.MasterConversionService;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.ValueKeyword;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings( { "rawtypes", "unchecked" } )

public final class NumericRangeValidationService extends ValidationService
{
    private Comparable min;
    private Comparable max;
    
    @Override
    protected final void init()
    {
        super.init();
        
        final PropertyDef property = context( PropertyDef.class );
        final Class<?> type = property.getTypeClass();
        final NumericRange rangeConstraintAnnotation = property.getAnnotation( NumericRange.class );
        final MasterConversionService converter = property.service( MasterConversionService.class );

        final String minStr = rangeConstraintAnnotation.min();
        
        if( minStr != null )
        {
            this.min = ( minStr.length() > 0 ? (Comparable) converter.convert( minStr, type ) : null );
        }
        
        final String maxStr = rangeConstraintAnnotation.max();
        
        if( maxStr != null )
        {
            this.max = ( maxStr.length() > 0 ? (Comparable) converter.convert( maxStr, type ) : null );
        }
    }

    @Override
    public final Status validate()
    {
        final Value<Comparable> value = context( Value.class );
        final Comparable val = (Comparable) value.content( true );
        
        if( val != null )
        {
            final ValueProperty property = value.definition();
            
            if( this.min != null && val.compareTo( this.min ) < 0 )
            {
                final String msg = NLS.bind( Resources.smallerThanMinimumMessage, val, normalizeForDisplay( property, this.min ) );
                return Status.createErrorStatus( msg );
            }
            
            if( this.max != null && val.compareTo( this.max ) > 0 )
            {
                final String msg = NLS.bind( Resources.largerThanMaxiumMessage, val, normalizeForDisplay( property, this.max ) );
                return Status.createErrorStatus( msg );                
            }
        }
        
        return Status.createOkStatus();
    }
    
    private String normalizeForDisplay( final ValueProperty property,
                                        final Object value )
    {
        String result = property.encodeKeywords( value.toString() );
        
        ValueKeyword keyword = property.getKeyword( result );
        
        if( keyword != null )
        {
            result = keyword.toDisplayString();
        }
        
        return result;
    }
    
    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            final ValueProperty property = context.find( ValueProperty.class );
            return ( property != null && property.hasAnnotation( NumericRange.class ) && Number.class.isAssignableFrom( property.getTypeClass() ) );
        }
    }

    private static final class Resources extends NLS
    {
        public static String smallerThanMinimumMessage;
        public static String largerThanMaxiumMessage;

        static
        {
            initializeMessages( NumericRangeValidationService.class.getName(), Resources.class );
        }
    }

}
