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

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.FoldingXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.ui.def.internal.FormPartIncludeReferenceService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "include" )
@Image( small = "org.eclipse.sapphire.ui/images/objects/include.gif" )
@GenerateImpl

public interface IFormPartInclude

    extends IFormPartDef
    
{
    ModelElementType TYPE = new ModelElementType( IFormPartInclude.class );
    
    // *** Part ***

    @Reference( target = IFormPartDef.class, service = FormPartIncludeReferenceService.class )
    @Label( standard = "part" )
    @PossibleValues( property = "/PartDefs/Id")
    @NonNullValue
    @CustomXmlValueBinding( impl = FoldingXmlValueBindingImpl.class, params = "part" )
    
    ValueProperty PROP_PART = new ValueProperty( TYPE, "Part" );
    
    ReferenceValue<IFormPartDef> getPart();
    void setPart( String part );
    
    // *** Params ***
    
    @Label( standard = "params" )
    @Type( base = ISapphireParam.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "param", type = ISapphireParam.class ) )
    
    ListProperty PROP_PARAMS = new ListProperty( TYPE, "Params" );
    
    ModelElementList<ISapphireParam> getParams();

}