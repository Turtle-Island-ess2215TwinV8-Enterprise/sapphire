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
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

@Label( standard = "documentation" )
@GenerateImpl

public interface ISapphireDocumentationDef

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ISapphireDocumentationDef.class );
 
    // *** Id ***
    
    @Label( standard = "ID" )
    @XmlBinding( path = "id" )
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );
    
    Value<String> getId();
    void setId( String id );

    // *** title ***
    
    @Label( standard = "title" )
    @XmlBinding( path = "title" )
    
    ValueProperty PROP_TITLE = new ValueProperty( TYPE, "Title" ); //$NON-NLS-1$
    
    Value<String> getTitle();
    void setTitle( String title );

    // *** content ***
    
    @Label( standard = "content" )
    @XmlBinding( path = "content" )
    @NonNullValue

    ValueProperty PROP_CONTENT = new ValueProperty( TYPE, "Content" ); //$NON-NLS-1$
    
    Value<String> getContent();
    void setContent( String content );
    
    // *** related topics ***
    
    @Type( base = ISapphireDocumentationTopicDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "topic", type = ISapphireDocumentationTopicDef.class ) )
                             
    ListProperty PROP_TOPICS = new ListProperty( TYPE, "Topics" );
    
    ModelElementList<ISapphireDocumentationTopicDef> getTopics();

}
