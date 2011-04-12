/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.validation.internal;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueKeyword;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class NumericRangeValidationService<T extends Comparable<T>>

    extends ModelPropertyValidationService<Value<T>>

{
    private final T min;
    private final T max;
    
    public NumericRangeValidationService( final T minValue,
                                          final T maxValue )
    {
        this.min = minValue;
        this.max = maxValue;
    }
    
    @Override
    public IStatus validate()
    {
        final Value<T> value = target();
        final T val = value.getContent( true );
        
        if( val != null )
        {
            final ValueProperty property = value.getProperty();
            
            if( this.min != null && val.compareTo( this.min ) < 0 )
            {
                final String msg = NLS.bind( Resources.smallerThanMinimumMessage, val, normalizeForDisplay( property, this.min ) );
                return createErrorStatus( msg );
            }
            
            if( this.max != null && val.compareTo( this.max ) > 0 )
            {
                final String msg = NLS.bind( Resources.largerThanMaxiumMessage, val, normalizeForDisplay( property, this.max ) );
                return createErrorStatus( msg );                
            }
        }
        
        return Status.OK_STATUS;
    }
    
    private String normalizeForDisplay( final ValueProperty property,
                                        final T value )
    {
        String result = property.encodeKeywords( value.toString() );
        
        ValueKeyword keyword = property.getKeyword( result );
        
        if( keyword != null )
        {
            result = keyword.toDisplayString();
        }
        
        return result;
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            return ( property instanceof ValueProperty && property.hasAnnotation( NumericRange.class ) && Number.class.isAssignableFrom( property.getTypeClass() ) );
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            final Class<?> type = property.getTypeClass();
            final NumericRange rangeConstraintAnnotation = property.getAnnotation( NumericRange.class );
            final String minStr = rangeConstraintAnnotation.min();
            final String maxStr = rangeConstraintAnnotation.max();
            
            if( minStr != null || maxStr != null )
            {
                try
                {
                    if( Integer.class.isAssignableFrom( type ) )
                    {
                        final Integer min = ( minStr.length() > 0 ? Integer.valueOf( minStr ) : null );
                        final Integer max = ( maxStr.length() > 0 ? Integer.valueOf( maxStr ) : null );
                        return new NumericRangeValidationService<Integer>( min, max );
                    }
                    else if( Long.class.isAssignableFrom( type ) )
                    {
                        final Long min = ( minStr.length() > 0 ? Long.valueOf( minStr ) : null );
                        final Long max = ( maxStr.length() > 0 ? Long.valueOf( maxStr ) : null );
                        return new NumericRangeValidationService<Long>( min, max );
                    }
                    else if( Float.class.isAssignableFrom( type ) )
                    {
                        final Float min = ( minStr.length() > 0 ? Float.valueOf( minStr ) : null );
                        final Float max = ( maxStr.length() > 0 ? Float.valueOf( maxStr ) : null );
                        return new NumericRangeValidationService<Float>( min, max );
                    }
                    else if( Double.class.isAssignableFrom( type ) )
                    {
                        final Double min = ( minStr.length() > 0 ? Double.valueOf( minStr ) : null );
                        final Double max = ( maxStr.length() > 0 ? Double.valueOf( maxStr ) : null );
                        return new NumericRangeValidationService<Double>( min, max );
                    }
                    else if( BigInteger.class.isAssignableFrom( type ) )
                    {
                        final BigInteger min = ( minStr.length() > 0 ? new BigInteger( minStr ) : null );
                        final BigInteger max = ( maxStr.length() > 0 ? new BigInteger( maxStr ) : null );
                        return new NumericRangeValidationService<BigInteger>( min, max );
                    }
                    else if( BigDecimal.class.isAssignableFrom( type ) )
                    {
                        final BigDecimal min = ( minStr.length() > 0 ? new BigDecimal( minStr ) : null );
                        final BigDecimal max = ( maxStr.length() > 0 ? new BigDecimal( maxStr ) : null );
                        return new NumericRangeValidationService<BigDecimal>( min, max );
                    }
                }
                catch( NumberFormatException e )
                {
                    SapphireModelingFrameworkPlugin.log( e );
                }
            }
            
            return null;
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