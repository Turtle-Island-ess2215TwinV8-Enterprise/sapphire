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

package org.eclipse.sapphire.ui.form.editors.masterdetails;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.DataService;
import org.eclipse.sapphire.ui.PartValidationEvent;
import org.eclipse.sapphire.ui.PartVisibilityEvent;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePart.PartEvent;
import org.eclipse.sapphire.ui.SapphirePart.PartInitializationEvent;
import org.eclipse.sapphire.ui.SectionPart;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode.NodeListEvent;

/**
 * Produces a problem-annotated traversal order through the content outline, which can be used to find the next
 * error or warning from any location in the content outline.
 * 
 * <p>An implementation of this service is provided with Sapphire. This service is not intended to
 * be implemented by adopters.</p>
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProblemsTraversalService extends DataService<ProblemsTraversalServiceData>
{
    private final Listener listener = new Listener()
    {
        @Override
        public void handle( final Event event )
        {
            if( event instanceof PartEvent )
            {
                final SapphirePart part = ( (PartEvent) event ).part();

                if( event instanceof NodeListEvent )
                {
                    refresh();
                }
                else if( part instanceof SectionPart && ( event instanceof PartValidationEvent || event instanceof PartVisibilityEvent ) )
                {
                    refresh();
                }
            }
        }
    };
    
    private boolean pageInitializationCompleted;
    
    @Override
    protected void initDataService()
    {
        final MasterDetailsEditorPagePart page = context( MasterDetailsEditorPagePart.class );
        
        this.pageInitializationCompleted = page.initialized();
        
        if( ! this.pageInitializationCompleted )
        {
            // Defer initialization of this service until after the editor page has completed its 
            // initialization. Request for next problem prior to this will return null.
            
            page.attach
            (
                new FilteredListener<PartInitializationEvent>()
                {
                    @Override
                    protected void handleTypedEvent( final PartInitializationEvent event )
                    {
                        page.detach( this );
                        ProblemsTraversalService.this.pageInitializationCompleted = true;
                        refresh();
                    }
                }
            );
        }
    }

    @Override
    protected ProblemsTraversalServiceData compute()
    {
        if( this.pageInitializationCompleted )
        {
            return new ProblemsTraversalServiceData( context( MasterDetailsEditorPagePart.class ), this.listener );
        }
        else
        {
            return ProblemsTraversalServiceData.EMPTY;
        }
    }
    
    public MasterDetailsContentNode findNextProblem( final MasterDetailsContentNode reference,
                                                     final Status.Severity severity )
    {
        if( reference == null || ! ( severity == Status.Severity.ERROR || severity == Status.Severity.WARNING ) )
        {
            throw new IllegalArgumentException();
        }
        
        return data().findNextProblem( reference, severity );
    }
    
    public MasterDetailsContentNode findNextError( final MasterDetailsContentNode reference )
    {
        if( reference == null )
        {
            throw new IllegalArgumentException();
        }

        return data().findNextError( reference );
    }
    
    public MasterDetailsContentNode findNextWarning( final MasterDetailsContentNode reference )
    {
        if( reference == null )
        {
            throw new IllegalArgumentException();
        }

        return data().findNextWarning( reference );
    }
    
}
