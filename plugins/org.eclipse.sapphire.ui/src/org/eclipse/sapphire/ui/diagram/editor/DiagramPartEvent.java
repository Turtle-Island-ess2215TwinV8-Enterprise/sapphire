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

package org.eclipse.sapphire.ui.diagram.editor;

import org.eclipse.sapphire.ui.ISapphirePart;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class DiagramPartEvent
{
    private final ISapphirePart part;
    
    public DiagramPartEvent( final ISapphirePart part )
    {
        this.part = part;
    }
    
    public final ISapphirePart getPart()
    {
        return this.part;
    }
    
}