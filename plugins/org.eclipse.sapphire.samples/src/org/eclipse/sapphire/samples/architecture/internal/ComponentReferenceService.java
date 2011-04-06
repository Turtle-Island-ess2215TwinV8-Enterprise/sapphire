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

package org.eclipse.sapphire.samples.architecture.internal;

import org.eclipse.sapphire.modeling.ReferenceService;
import org.eclipse.sapphire.samples.architecture.IArchitecture;
import org.eclipse.sapphire.samples.architecture.IComponent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ComponentReferenceService 

    extends ReferenceService
    
{
    @Override
    public Object resolve( final String reference ) 
    {
        if( reference != null )
        {
            final IArchitecture arch = element().nearest( IArchitecture.class );
            
            for( IComponent component : arch.getComponents() )
            {
                if( reference.equals( component.getName().getText() ) )
                {
                    return component;
                }
            }
        }
        
        return null;
    }

}