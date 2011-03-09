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
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.PredefinedColoredAreas;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;
import org.eclipse.sapphire.ui.diagram.DiagramDropTargetService;
import org.eclipse.sapphire.ui.diagram.def.IDiagramLabelDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeImageDef;
import org.eclipse.sapphire.ui.diagram.def.ImagePlacement;
import org.eclipse.sapphire.ui.diagram.editor.DiagramGeometryWrapper;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramPropertyKeys;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireAddNodeFeature extends AbstractAddShapeFeature 
{
    private static final IColorConstant DEFAULT_TEXT_FOREGROUND = new ColorConstant(51, 51, 153);
    private static final IColorConstant DEFAULT_NODE_FOREGROUND = new ColorConstant(51, 51, 153);
    private static final int DEFAULT_NODE_WIDTH = 100;
    private static final int DEFAULT_NODE_HEIGHT = 30;
    private static final int DEFAULT_TEXT_HEIGHT = 20;
    private static int defaultX = 50;
    private static int defaultY = 50;
    private static int xInc = 100;
    private static int yInc = 0;
    private DiagramNodeTemplate nodeTemplate;
	
	public SapphireAddNodeFeature(IFeatureProvider fp, DiagramNodeTemplate nodeTemplate)
	{
		super(fp);
		this.nodeTemplate = nodeTemplate;
	}
	
	public boolean canAdd(IAddContext context) 
	{
		Object newObj = context.getNewObject();
		if (newObj instanceof DiagramNodePart)
		{
			return true;
		}
		else 
		{
			DiagramDropTargetService dropService = nodeTemplate.getDropTargetService();
			if (dropService != null && dropService.accept(newObj))
			{
				return true;
			}
		}
		return false;
	}

	public PictogramElement add(IAddContext context)
	{
		Object newObj = context.getNewObject();
		DiagramNodePart nodePart = null;
		if (newObj instanceof DiagramNodePart)
		{
			nodePart = (DiagramNodePart)context.getNewObject();
		}
		else 
		{
			DiagramDropTargetService dropService = nodeTemplate.getDropTargetService();
			nodePart = (DiagramNodePart)dropService.createModel(this.nodeTemplate, newObj);
		}
		final Diagram targetDiagram = (Diagram) context.getTargetContainer();
		
        // define a default size for the shape
        IDiagramNodeDef nodeDef = (IDiagramNodeDef)nodePart.getDefinition();
        int nodew = getNodeWidth(nodePart);
        int nodeh = getNodeHeight(nodePart);
        int width = nodew > 0 ? nodew : DEFAULT_NODE_WIDTH;
        int height = nodeh > 0 ? nodeh : DEFAULT_NODE_HEIGHT;
        
        int x, y;
        if (context.getX() != -1)
        {
        	x = context.getX();
        }
        else
        {
        	x = defaultX;
        	defaultX += xInc;
        }
        if (context.getY() != -1)
        {
        	y = context.getY();
        }
        else
        {
        	y = defaultY;
        	defaultY += yInc;
        }

        IPeCreateService peCreateService = Graphiti.getPeCreateService();
        ContainerShape containerShape =  peCreateService.createContainerShape(targetDiagram, true);
        IGaService gaService = Graphiti.getGaService();
        
        // TODO clean this up
        // The temporary logic is to create a default rectangle if no icon is associated with the node
        if (nodePart.getImageId() == null)
        {
            // create and set graphics algorithm
            RoundedRectangle rectangle = gaService.createRoundedRectangle(containerShape, 8, 8);
            rectangle.setForeground(manageColor(DEFAULT_NODE_FOREGROUND));
            gaService.setRenderingStyle(rectangle, PredefinedColoredAreas.getBlueWhiteGlossAdaptions());
            rectangle.setLineWidth(1);
            gaService.setLocationAndSize(rectangle, x, y, width, height);
 
            // create link and wire it
            link(containerShape, nodePart);        		
        }
        else
        {
        	Rectangle rectangle = gaService.createRectangle(containerShape);
        	rectangle.setFilled(false);
        	rectangle.setLineVisible(false);
        	gaService.setLocationAndSize(rectangle, x, y, width, height);
        	
        	link(containerShape, nodePart);
        	
            // Shape with Image
            {
            	Shape shape = peCreateService.createShape(containerShape, false);
            	String imageId = nodePart.getImageId();
            	Image image = gaService.createImage(shape, imageId);
            	
            	Point imageLocation = getImageLocation(nodePart);
        		Graphiti.getPeService().setPropertyValue(containerShape, 
        				SapphireDiagramPropertyKeys.NODE_IMAGE_ID, imageId);

    	        gaService.setLocationAndSize(image, imageLocation.getX(), imageLocation.getY(),
    	        		nodePart.getImageWidth(), nodePart.getImageHeight());
            }        	
        }

        if (nodeDef.getLabel().element() != null)
        {
            // create shape for text
            Shape shape = peCreateService.createShape(containerShape, false);
 
            // create and set text graphics algorithm
            Text text = gaService.createDefaultText(shape, nodePart.getLabel());
            text.setForeground(manageColor(DEFAULT_TEXT_FOREGROUND));            
            text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
            text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);

            Point labelLocation = getLabelLocation(nodePart);
            int labelWidth = nodePart.getLabelWidth();
            int labelHeight = nodePart.getLabelHeight();
            int lwidth = labelWidth > 0 ? labelWidth : width;
            int lheight = labelHeight > 0 ? labelHeight : (nodePart.getImageId() == null ? height : DEFAULT_TEXT_HEIGHT);
            
            gaService.setLocationAndSize(text, labelLocation.getX(), labelLocation.getY(), 
            		lwidth, lheight);
 
            // create link and wire it
            link(shape, nodePart);            
        }
        
        // add a chopbox anchor to the shape
        peCreateService.createChopboxAnchor(containerShape);
        
        // Save the node bounds
        DiagramGeometryWrapper diagramGeometry = 
        	((SapphireDiagramFeatureProvider)getFeatureProvider()).getDiagramGeometry();
        diagramGeometry.addNode(nodePart, x, y, width, height);
        
		return containerShape;
	}

	private int getNodeWidth(DiagramNodePart nodePart)
	{
		if (nodePart.getNodeWidth() > 0)
		{
			return nodePart.getNodeWidth();
		}
		
		int width = 0;
		int labelWidth = nodePart.getLabelWidth();
		if (nodePart.getImageId() != null)
		{
			int imageWidth = nodePart.getImageWidth();
			
			ImagePlacement imagePlacement = nodePart.getImagePlacement();
			if (imagePlacement == ImagePlacement.TOP || imagePlacement == ImagePlacement.BOTTOM)
			{
				width = Math.max(labelWidth, imageWidth);
			}
			else if (imagePlacement == ImagePlacement.LEFT || imagePlacement == ImagePlacement.RIGHT)
			{
				int horizaontalSpacing = nodePart.getHorizontalSpacing();
				width = labelWidth + imageWidth + horizaontalSpacing;
			}
		}
		else 
		{
			width = labelWidth;
		}
		return width;
	}

	private int getNodeHeight(DiagramNodePart nodePart)
	{
		if (nodePart.getNodeHeight() > 0)
		{
			return nodePart.getNodeHeight();
		}
		int height = 0;
		int labelHeight = nodePart.getLabelHeight();
		if (nodePart.getImageId() != null)
		{
			int imageHeight = nodePart.getImageHeight();
			
			ImagePlacement imagePlacement = nodePart.getImagePlacement();
			if (imagePlacement == ImagePlacement.TOP || imagePlacement == ImagePlacement.BOTTOM)
			{
				int verticalSpacing = nodePart.getVerticalSpacing();
				height = labelHeight + imageHeight + verticalSpacing;
			}
			else if (imagePlacement == ImagePlacement.LEFT || imagePlacement == ImagePlacement.RIGHT)
			{
				height = Math.max(labelHeight, imageHeight);
			}
		}
		else 
		{
			height = labelHeight;
		}
		return height;
	}
	
	private Point getImageLocation(DiagramNodePart nodePart)
	{
		ImagePlacement imagePlacement = nodePart.getImagePlacement();
		if (imagePlacement == ImagePlacement.TOP || imagePlacement == ImagePlacement.LEFT)
		{
			return new Point(0, 0);
		}
		else if (imagePlacement == ImagePlacement.BOTTOM)
		{
			int labelHeight = nodePart.getLabelHeight();
			int horizontalSpacing = nodePart.getHorizontalSpacing();
			return new Point(labelHeight + horizontalSpacing, 0);
		}
		else if (imagePlacement == ImagePlacement.RIGHT )
		{
			int labelWidth = nodePart.getLabelWidth();
			int verticalSpacing = nodePart.getVerticalSpacing();
			return new Point(labelWidth + verticalSpacing, 0);			
		}
		return new Point(0, 0);
	}
	
	private Point getLabelLocation(DiagramNodePart nodePart)
	{
		ImagePlacement imagePlacement = nodePart.getImagePlacement();
		if (imagePlacement == ImagePlacement.TOP)
		{
			int imageHeight = nodePart.getImageHeight();
			int verticalSpacing = nodePart.getVerticalSpacing();			
			return new Point(0, imageHeight + verticalSpacing);
		}
		else if (imagePlacement == ImagePlacement.BOTTOM || imagePlacement == ImagePlacement.RIGHT)
		{
			return new Point(0, 0);
		}
		else if (imagePlacement == ImagePlacement.LEFT )
		{
			int imageWidth = nodePart.getImageWidth();
			int horizontalSpacing = nodePart.getHorizontalSpacing();
			return new Point(imageWidth + horizontalSpacing, 0);			
		}
		return new Point(0, 0);
	}
}