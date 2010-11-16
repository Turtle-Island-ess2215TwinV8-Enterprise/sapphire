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

package org.eclipse.sapphire.samples.contacts.internal;

import java.util.SortedSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.PossibleValuesService;
import org.eclipse.sapphire.samples.contacts.IContact;
import org.eclipse.sapphire.samples.contacts.IContactsDatabase;
import org.eclipse.sapphire.samples.contacts.ISendContactOp;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SendContactToPossibleValuesProvider

    extends PossibleValuesService
    
{
    @Override
    protected void fillPossibleValues( final SortedSet<String> values )
    {
        final ISendContactOp op = (ISendContactOp) element();
        final IContact contact = op.getContact().content();
        
        if( contact != null )
        {
            for( IContact c : contact.nearest( IContactsDatabase.class ).getContacts() )
            {
                final String email = c.getEMail().getText();
                
                if( email != null )
                {
                    values.add( email );
                }
            }
        }
    }

    @Override
    public int getInvalidValueSeverity( String invalidValue )
    {
        return IStatus.OK;
    }
    
}
