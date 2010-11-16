/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "listener" )
@GenerateImpl

public interface ISapphirePartListenerDef

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ISapphirePartListenerDef.class );
    
    // *** ListenerClass ***
    
    @Reference( target = Class.class )
    @Label( standard = "listener class" )
    @XmlBinding( path = "" )
    
    ValueProperty PROP_LISTENER_CLASS = new ValueProperty( TYPE, "ListenerClass" );
    
    ReferenceValue<Class<?>> getListenerClass();
    void setListenerClass( String listenerClass );

}
