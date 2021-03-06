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

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "page" )

public interface PageBookKeyMapping extends CompositeDef
{
    ElementType TYPE = new ElementType( PageBookKeyMapping.class );
    
    // *** Key ***
    
    @Label( standard = "key" )
    @Required
    @XmlBinding( path = "key" )
    
    ValueProperty PROP_KEY = new ValueProperty( TYPE, "Key" );
    
    Value<String> getKey();
    void setKey( String key );

}
