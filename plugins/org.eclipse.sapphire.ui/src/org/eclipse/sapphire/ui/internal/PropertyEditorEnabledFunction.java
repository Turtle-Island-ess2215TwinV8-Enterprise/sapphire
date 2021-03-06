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

package org.eclipse.sapphire.ui.internal;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyEnablementEvent;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionException;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.ui.PartFunctionContext;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphirePart;

/**
 * Determines if the property associated with the property editor is enabled. Can only be used in the 
 * context of a property editor.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertyEditorEnabledFunction extends Function
{
    @Override
    public String name()
    {
        return "Enabled";
    }

    @Override
    public FunctionResult evaluate( final FunctionContext context )
    {
        if( context instanceof PartFunctionContext )
        {
            final PartFunctionContext fc = (PartFunctionContext) context;
            final SapphirePart part = fc.part();
            
            if( part instanceof PropertyEditorPart )
            {
                final Property property = ( (PropertyEditorPart) part ).property();
                
                return new FunctionResult( this, fc )
                {
                    private Listener listener;
                    
                    @Override
                    protected void init()
                    {
                        this.listener = new FilteredListener<PropertyEnablementEvent>()
                        {
                            @Override
                            protected void handleTypedEvent( final PropertyEnablementEvent event )
                            {
                                refresh();
                            }
                        };
                        
                        property.attach( this.listener );
                    }

                    @Override
                    protected Object evaluate()
                    {
                        return property.enabled();
                    }
                    
                    @Override
                    public void dispose()
                    {
                        super.dispose();
                        
                        property.detach( this.listener );
                        this.listener = null;
                    }
                };
            }
        }
        
        throw new FunctionException( Resources.contextPropertyEditorNotFound );
    }
    
    private static final class Resources extends NLS
    {
        public static String contextPropertyEditorNotFound;
        
        static
        {
            initializeMessages( PropertyEditorEnabledFunction.class.getName(), Resources.class );
        }
    }
    
}
