/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.actions;

import java.util.List;

import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.gef.diagram.editor.DiagramRenderingContext;
import org.eclipse.sapphire.ui.gef.diagram.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramModel;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramDirectEditActionHandler extends SapphireDiagramActionHandler
{

	@Override
	public boolean canExecute(Object obj) 
	{
		return true;
	}

	@Override
	protected Object run(SapphireRenderingContext context) 
	{
		DiagramRenderingContext diagramContext = (DiagramRenderingContext)context;
		SapphireDiagramEditor diagramEditor = diagramContext.getDiagramEditor();
		if (diagramEditor != null)
		{
			List<SapphirePart> parts = diagramEditor.getSelectedParts();
			DiagramModel model = diagramEditor.getDiagramModel();
			if (parts.size() == 1)
			{
				SapphirePart part = parts.get(0);
				if (part instanceof DiagramNodePart)
				{
					model.handleDirectEditing((DiagramNodePart)part);
				}
				else if (part instanceof DiagramConnectionPart && !(part instanceof DiagramImplicitConnectionPart))
				{
					model.handleDirectEditing((DiagramConnectionPart)part);
				}
			}
		}
		return null;
	}

}