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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class CountConstraintValidationService extends ValidationService
{
    private CountConstraint constraint;
    
    @Override
    protected void init()
    {
        super.init();
        
        this.constraint = context( PropertyDef.class ).getAnnotation( CountConstraint.class );
    }

    @Override
    public Status validate()
    {
        final ElementList<?> list = context( ElementList.class );
        final int count = list.size();
        String message = null;
        
        if( count < this.constraint.min() )
        {
            if( this.constraint.min() == 1 )
            {
                message = Resources.bind( Resources.countConstraintTooFewAtLeastOne, 
                                          context( PropertyDef.class ).getType().getLabel( true, CapitalizationType.NO_CAPS, false ) );
            }
            else
            {
                message = Resources.bind( Resources.countConstraintTooFew, 
                                          context( PropertyDef.class ).getType().getLabel( true, CapitalizationType.NO_CAPS, false ), 
                                          String.valueOf( this.constraint.min() ) );
            }
        }
        else if( count > this.constraint.max() )
        {
            message = Resources.bind( Resources.countConstraintTooMany,
                                      context( PropertyDef.class ).getType().getLabel( true, CapitalizationType.NO_CAPS, false ), 
                                      String.valueOf( this.constraint.max() ) );
        }
        
        if( message == null )
        {
            return Status.createOkStatus();
        }
        else
        {
            return Status.createErrorStatus( message );
        }
    }
    
    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            final ListProperty property = context.find( ListProperty.class );
            return ( property != null && property.hasAnnotation( CountConstraint.class ) );
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String countConstraintTooFew;
        public static String countConstraintTooFewAtLeastOne;
        public static String countConstraintTooMany;
        
        static
        {
            initializeMessages( CountConstraintValidationService.class.getName(), Resources.class );
        }
    }

}