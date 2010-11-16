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

package org.eclipse.sapphire.samples.gallery;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface IMultiSelectListGalleryItem

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( IMultiSelectListGalleryItem.class );
    
    // *** Item ***
    
    @Label( standard = "item" )
    @XmlBinding( path = "" )
    @NoDuplicates
    @PossibleValues( values = { "Red", "Orange", "Yellow", "Green", "Blue", "Violet" }, invalidValueMessage = "{0} is not a valid color." )

    ValueProperty PROP_ITEM = new ValueProperty( TYPE, "Item" );
    
    Value<String> getItem();
    void setItem( String value );
    
}
