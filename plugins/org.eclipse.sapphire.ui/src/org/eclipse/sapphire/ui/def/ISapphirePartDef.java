/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [bugzilla 329114] rewrite context help binding feature
 ******************************************************************************/

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.ui.def.internal.SapphirePartDefHintsListBindingImpl;
import org.eclipse.sapphire.ui.def.internal.SapphirePartDefMethods;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface ISapphirePartDef

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ISapphirePartDef.class );
    
    String HINT_EXPAND_VERTICALLY = "expand.vertically";
    String HINT_HIDE_IF_DISABLED = "hide.if.disabled";
    String HINT_WIDTH = "width";
    String HINT_HEIGHT = "height";
    String HINT_PREFER_FORM_STYLE = "prefer.form.style";
    
    // *** Documentation ***
    
    @Type( base = ISapphireDocumentationDef.class )
    @XmlBinding( path = "documentation" )
    
    ElementProperty PROP_DOCUMENTATION_DEF = new ElementProperty( TYPE, "DocumentationDef" );
    
    ModelElementHandle<ISapphireDocumentationDef> getDocumentationDef();

    // *** DocumentationRef ***
    
    @Type( base = ISapphireDocumentationRef.class )
    @XmlBinding( path = "documentation-ref" )
    
    ElementProperty PROP_DOCUMENTATION_REF = new ElementProperty( TYPE, "DocumentationRef" );
    
    ModelElementHandle<ISapphireDocumentationRef> getDocumentationRef();

    // *** Hints ***
    
    @Label( standard = "hints" )
    @Type( base = ISapphireHint.class )
    @CustomXmlListBinding( impl = SapphirePartDefHintsListBindingImpl.class )
    
    ListProperty PROP_HINTS = new ListProperty( TYPE, "Hints" );
    
    ModelElementList<ISapphireHint> getHints();
    
    // *** Method : getHint ***
    
    @DelegateImplementation( SapphirePartDefMethods.class )
    
    String getHint( String name );
    
    // *** Method : getHint ***
    
    @DelegateImplementation( SapphirePartDefMethods.class )
    
    boolean getHint( String name,
                     boolean defaultValue );

    // *** Method : getHint ***
    
    @DelegateImplementation( SapphirePartDefMethods.class )
    
    int getHint( String name,
                 int defaultValue );
    
    // *** Listeners ***
    
    @Label( standard = "listeners" )
    @Type( base = ISapphirePartListenerDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "listener", type = ISapphirePartListenerDef.class ) )
    
    ListProperty PROP_LISTENERS = new ListProperty( TYPE, "Listeners" );
    
    ModelElementList<ISapphirePartListenerDef> getListeners();
    
    // *** Actions ***
    
    @Type( base = ISapphireActionDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "action", type = ISapphireActionDef.class ) )
    @Label( standard = "action" )
    
    ListProperty PROP_ACTIONS = new ListProperty( TYPE, "Actions" );
    
    ModelElementList<ISapphireActionDef> getActions();
    
    // *** ActionHandlers ***
    
    @Type( base = ISapphireActionHandlerDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "action-handler", type = ISapphireActionHandlerDef.class ) )
    @Label( standard = "action handlers" )
    
    ListProperty PROP_ACTION_HANDLERS = new ListProperty( TYPE, "ActionHandlers" );
    
    ModelElementList<ISapphireActionHandlerDef> getActionHandlers();
    
    // *** ActionHandlerFactories ***
    
    @Type( base = ISapphireActionHandlerFactoryDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "action-handler-factory", type = ISapphireActionHandlerFactoryDef.class ) )
    @Label( standard = "action handler factories" )
    
    ListProperty PROP_ACTION_HANDLER_FACTORIES = new ListProperty( TYPE, "ActionHandlerFactories" );
    
    ModelElementList<ISapphireActionHandlerFactoryDef> getActionHandlerFactories();
    
    // *** ActionHandlerFilters ***
    
    @Type( base = ISapphireActionHandlerFilterDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "action-handler-filter", type = ISapphireActionHandlerFilterDef.class ) )
    @Label( standard = "action handler filters" )
    
    ListProperty PROP_ACTION_HANDLER_FILTERS = new ListProperty( TYPE, "ActionHandlerFilters" );
    
    ModelElementList<ISapphireActionHandlerFilterDef> getActionHandlerFilters();
    
}
