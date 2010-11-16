/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.internal.DocumentationRefMethods;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

@GenerateImpl

public interface ISapphireDocumentationRef

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ISapphireDocumentationRef.class );
    
    // *** Id ***
    
    @Label( standard = "ID" )
    @XmlBinding( path = "id" )
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );
    
    Value<String> getId();
    void setId( String id );
    
    // *** Method : resolve ***
    
    @DelegateImplementation( DocumentationRefMethods.class )
    
    ISapphireDocumentationDef resolve();

}
