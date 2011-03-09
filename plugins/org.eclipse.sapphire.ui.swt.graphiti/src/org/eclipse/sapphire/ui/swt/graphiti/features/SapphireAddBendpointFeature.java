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

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddBendpointContext;
import org.eclipse.graphiti.features.impl.DefaultAddBendpointFeature;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramGeometryWrapper;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireAddBendpointFeature extends DefaultAddBendpointFeature 
{
	public SapphireAddBendpointFeature(IFeatureProvider fp)
	{
		super(fp);
	}
	
	@Override
	public void addBendpoint(IAddBendpointContext context) 
	{
		super.addBendpoint(context);
		
		FreeFormConnection freeFormConnection = context.getConnection();
		Object bo = getBusinessObjectForPictogramElement(freeFormConnection);
		if (bo instanceof DiagramConnectionPart)
		{
			DiagramGeometryWrapper dg = 
				((SapphireDiagramFeatureProvider)getFeatureProvider()).getDiagramGeometry();
			dg.addConnectionBendpoint((DiagramConnectionPart)bo, 
					context.getBendpointIndex(), context.getX(), context.getY());
		}		
	}	
}