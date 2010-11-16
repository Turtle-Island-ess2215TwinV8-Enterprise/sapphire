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

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "tab group" )
@GenerateImpl

public interface ISapphireTabGroupDef

    extends ISapphirePartDef
    
{
    ModelElementType TYPE = new ModelElementType( ISapphireTabGroupDef.class );
    
    // *** Tabs ***
    
    @Label( standard = "tabs" )
    @Type( base = ISapphireTabGroupItemDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "tab", type = ISapphireTabGroupItemDef.class ) )
    
    ListProperty PROP_TABS = new ListProperty( TYPE, "Tabs" );
    
    ModelElementList<ISapphireTabGroupItemDef> getTabs();

}
