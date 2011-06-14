/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti;

import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramRenderingContext extends SapphireRenderingContext 
{
    private GraphicsAlgorithmContainer ga;
    private SapphireDiagramEditor diagramEditor;
    
    public DiagramRenderingContext(ISapphirePart part, SapphireDiagramEditor diagramEditor,
            GraphicsAlgorithmContainer containerShape)
    {
        super(part, null);
        this.diagramEditor = diagramEditor;
        this.ga = containerShape;
    }
    
    public GraphicsAlgorithmContainer getGAContainer()
    {
        return this.ga;
    }
    
    public SapphireDiagramEditor getDiagramEditor()
    {
        return this.diagramEditor;
    }
}
