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

package org.eclipse.sapphire.ui.def.internal;

import org.eclipse.sapphire.modeling.xml.StandardXmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.ui.def.PageBookExtDef;
import org.eclipse.sapphire.ui.def.PageBookPartControlMethod;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PageBookPartDefControlMethodBinding

    extends XmlValueBindingImpl

{
    private static final StandardXmlNamespaceResolver NAMESPACE_RESOLVER = new StandardXmlNamespaceResolver( PageBookExtDef.TYPE );
    private static final XmlPath PATH_ENUM_CONTROLLER = new XmlPath( "enum-controller", NAMESPACE_RESOLVER );
    private static final XmlPath PATH_LIST_SELECTION_CONTROLLER = new XmlPath( "list-selection-controller", NAMESPACE_RESOLVER );
    
    @Override
    public String read()
    {
        final XmlElement el = xml( false );
        
        if( el.getChildNode( PATH_ENUM_CONTROLLER, false ) != null )
        {
            return PageBookPartControlMethod.ENUM_VALUE.name();
        }
        else if( el.getChildNode( PATH_LIST_SELECTION_CONTROLLER, false ) != null )
        {
            return PageBookPartControlMethod.LIST_SELECTION.name();
        }
        
        return null;
    }

    @Override
    public void write( final String value )
    {
        final XmlElement el = xml( true );
        
        if( PageBookPartControlMethod.ENUM_VALUE.name().equals( value ) )
        {
            el.removeChildNode( PATH_LIST_SELECTION_CONTROLLER );
            el.getChildNode( PATH_ENUM_CONTROLLER, true );
        }
        else if( PageBookPartControlMethod.LIST_SELECTION.name().equals( value ) )
        {
            el.removeChildNode( PATH_ENUM_CONTROLLER );
            el.getChildNode( PATH_LIST_SELECTION_CONTROLLER, true );
        }
        else
        {
            el.removeChildNode( PATH_ENUM_CONTROLLER );
            el.removeChildNode( PATH_LIST_SELECTION_CONTROLLER );
        }
    }

}
