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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.services.DependenciesService;
import org.eclipse.sapphire.services.DependenciesServiceData;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;

/**
 * Implementation of DependenciesService that exposes implied dependencies specified by the @NoDuplicates annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class UniqueValueDependenciesService extends DependenciesService
{
    @Override
    protected DependenciesServiceData compute()
    {
        return new DependenciesServiceData( new ModelPath( "#/" + context( PropertyDef.class ).name() ) );
    }

    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            final ValueProperty property = context.find( ValueProperty.class );
            final Element element = context.find( Element.class );
            
            if( property != null && property.hasAnnotation( NoDuplicates.class ) )
            {
                final Property parent = element.parent();
                
                if( parent != null && parent.definition() instanceof ListProperty )
                {
                    return true;
                }
            }
            
            return false;
        }
    }
    
}
