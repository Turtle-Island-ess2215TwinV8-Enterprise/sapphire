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

package org.eclipse.sapphire.ui;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;
import org.eclipse.sapphire.ui.def.SapphireActionType;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class SapphireActionHandler

    extends SapphireActionSystemPart
    
{
    public static final String EVENT_PRE_EXECUTE = "pre-execute";
    public static final String EVENT_POST_EXECUTE = "post-execute";
    
    private SapphireAction action;

    public void init( final SapphireAction action,
                      final ISapphireActionHandlerDef def )
    {
        super.init( def );
        
        this.action = action;
        
        if( getId() == null )
        {
            final StringBuilder buf = new StringBuilder();
            buf.append( getClass().getName() );
            buf.append( '#' );
            buf.append( System.identityHashCode( this ) );
            
            setId( buf.toString() );
        }
        
        if( getLabel() == null )
        {
            setLabel( this.action.getLabel() );
        }
    }
    
    public final SapphireAction getAction()
    {
        return this.action;
    }
    
    public final ISapphirePart getPart()
    {
        return this.action.getPart();
    }
    
    public final String getContext()
    {
        return this.action.getContext();
    }
    
    public final IModelElement getModelElement()
    {
        return getPart().getModelElement();
    }
    
    public final void execute( SapphireRenderingContext context )
    {
        if( getAction().getType() == SapphireActionType.TOGGLE )
        {
            setChecked( ! isChecked() );
        }
        
        notifyListeners( new Event( EVENT_PRE_EXECUTE ) );
        
        Object result = null;
        
        try
        {
            result = run( context );
        }
        catch( Exception e )
        {
            SapphireUiFrameworkPlugin.log( e );
        }
        
        notifyListeners( new PostExecuteEvent( result ) );
    }
    
    protected abstract Object run( SapphireRenderingContext context );
    
    public void dispose()
    {
    }
    
    public static class PostExecuteEvent extends Event
    {
        private final Object result;
        
        public PostExecuteEvent( final Object result )
        {
            super( EVENT_POST_EXECUTE );
            this.result = result;
        }
        
        public Object getResult()
        {
            return this.result;
        }
    }
    
}