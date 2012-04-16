/******************************************************************************
 * Copyright (c) 2012 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - Customized grid layer, grid state save and restore; 
 *                   DND fixes.
 *    Shenxue Zhou - [bugzilla 365019] - SapphireDiagramEditor does not work on 
 *                   non-workspace files 
 *    Gregory Amerson - [374022] - SapphireGraphicalEditor init with SapphireEditor
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.GridLayer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.SapphireHelpContext;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.SapphireUiDefFactory;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPartEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramPartListener;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.swt.gef.dnd.ObjectsTransferDropTargetListener;
import org.eclipse.sapphire.ui.swt.gef.dnd.SapphireTemplateTransferDropTargetListener;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramModelBase;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.swt.gef.parts.DiagramNodeEditPart;
import org.eclipse.sapphire.ui.swt.gef.parts.SapphireDiagramEditorEditPartFactory;
import org.eclipse.sapphire.ui.util.SapphireHelpSystem;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditor extends GraphicalEditorWithFlyoutPalette {

    private DiagramLayoutPersistenceService layoutPersistenceService;
	private PaletteRoot root;
    private IDiagramEditorPageDef diagramPageDef;
    private SapphireDiagramEditorPagePart diagramPart;
    private DiagramModel diagramModel;
    private SapphireDiagramPartListener diagramPartListener;
    private List<ISapphirePart> selectedParts = null;
    private List<GraphicalEditPart> selectedEditParts = null;
    private boolean editorIsDirty = false;

	private Point mouseLocation;
	private DiagramConfigurationManager configManager;
	private GraphicalViewerKeyHandler graphicalViewerKeyHandler;
	private SapphireDiagramKeyHandler diagramKeyHandler;

	public SapphireDiagramEditor(
		final SapphireEditor editor, final IModelElement rootModelElement, final IPath pageDefinitionLocation )
	{
		final String bundleId = pageDefinitionLocation.segment( 0 );
        final String pageId = pageDefinitionLocation.lastSegment();
        final String relPath = pageDefinitionLocation.removeFirstSegments( 1 ).removeLastSegments( 1 ).toPortableString();
        
        final ISapphireUiDef def = SapphireUiDefFactory.load( bundleId, relPath );
        
        this.diagramPageDef = (IDiagramEditorPageDef) def.getPartDef( pageId, true, IDiagramEditorPageDef.class );

        this.diagramPart = new SapphireDiagramEditorPagePart();
		this.diagramPart.init( editor, rootModelElement, this.diagramPageDef, Collections.<String, String> emptyMap() );
        
		// Initialize layout persistence service
		this.layoutPersistenceService = SapphireDiagramEditorFactory.getLayoutPersistenceService(this.diagramPart);
		
        this.configManager = new DiagramConfigurationManager(this);
        
        this.diagramModel = new DiagramModel(diagramPart, this.configManager);

		setEditDomain(new DefaultEditDomain(this));

		this.diagramPartListener = new SapphireDiagramPartListener() 
        {
            @Override
            public void handleNodeUpdateEvent(final DiagramNodeEvent event)
            {
                updateNode((DiagramNodePart)event.getPart());
            }
            
            @Override
            public void handleNodeAddEvent(final DiagramNodeEvent event)
            {
                addNode((DiagramNodePart)event.getPart());
            }
            
            @Override
            public void handleNodeDeleteEvent(final DiagramNodeEvent event)
            {
                removeNode((DiagramNodePart)event.getPart());
            }

			@Override
		    public void handleNodeMoveEvent(final DiagramNodeEvent event)
		    {		    	
		    	DiagramNodePart nodePart = (DiagramNodePart)event.getPart();
		    	moveNode(nodePart);
		    	if (layoutPersistenceService.isNodePersisted(nodePart))
		    	{
		    		Bounds newBounds = nodePart.getNodeBounds();
		    		Bounds oldBounds = layoutPersistenceService.read(nodePart);
		    		if (oldBounds == null || newBounds.getX() != oldBounds.getX() || newBounds.getY() != oldBounds.getY())
		    		{
		    			markEditorDirty();
		    		}
		    		else
		    		{
		    			markEditorClean();
		    		}
		    	}
		    	else
		    	{
		    		markEditorDirty();
		    	}
		    }
			
			@Override
			public void handleConnectionUpdateEvent(final DiagramConnectionEvent event)
			{
				updateConnection((DiagramConnectionPart)event.getPart());
			}

            @Override
            public void handleConnectionEndpointEvent(final DiagramConnectionEvent event)
            {
                updateConnectionEndpoint((DiagramConnectionPart)event.getPart());
            }

            @Override
            public void handleConnectionAddEvent(final DiagramConnectionEvent event)
            {
                addConnectionIfPossible((DiagramConnectionPart)event.getPart());
            }

			@Override
			public void handleConnectionDeleteEvent(final DiagramConnectionEvent event)
			{
				removeConnection((DiagramConnectionPart)event.getPart());
			}
			
			@Override
		    public void handleConnectionAddBendpointEvent(final DiagramConnectionEvent event)
		    {
				DiagramConnectionPart connPart = (DiagramConnectionPart)event.getPart();
		    	updateConnectionBendpoint(connPart);
		    	markEditorForConnectionBendPointEvent(connPart);
		    }

			@Override
		    public void handleConnectionRemoveBendpointEvent(final DiagramConnectionEvent event)
		    {
				DiagramConnectionPart connPart = (DiagramConnectionPart)event.getPart();
		    	updateConnectionBendpoint(connPart);
		    	markEditorForConnectionBendPointEvent(connPart);
		    }
			
			@Override
		    public void handleConnectionMoveBendpointEvent(final DiagramConnectionEvent event)
		    {
				DiagramConnectionPart connPart = (DiagramConnectionPart)event.getPart();
		    	updateConnectionBendpoint(connPart);
		    	markEditorForConnectionBendPointEvent(connPart);
		    }
			
			@Override
		    public void handleConnectionResetBendpointsEvent(final DiagramConnectionEvent event)
		    {
				DiagramConnectionPart connPart = (DiagramConnectionPart)event.getPart();
		    	updateConnectionBendpoint(connPart);
		    	markEditorForConnectionBendPointEvent(connPart);
		    }

			@Override
		    public void handleConnectionMoveLabelEvent(final DiagramConnectionEvent event)
		    {
				DiagramConnectionPart connPart = (DiagramConnectionPart)event.getPart();
		    	updateConnectionMoveLabel(connPart);
		    	if (layoutPersistenceService.isConnectionPersisted(connPart))
		    	{
		    		org.eclipse.sapphire.ui.Point savedLabelPos = layoutPersistenceService.readConnectionLabelPosition(connPart);
		    		org.eclipse.sapphire.ui.Point newLabelPos = connPart.getLabelPosition();
		    		if (savedLabelPos != null && newLabelPos.getX() == savedLabelPos.getX() && 
		    				newLabelPos.getY() == savedLabelPos.getY())
		    		{
		    			markEditorClean();
		    		}
		    		else
		    		{
		    			markEditorDirty();
		    		}
		    	}
		    	else
		    	{
		    		markEditorDirty();
		    	}
		    }

			@Override
		    public void handleDirectEditEvent(final DiagramPartEvent event)
		    {
		    	selectAndDirectEditPart(event.getPart());
		    }
			
		    @Override
			public void handleGridStateChangeEvent(final DiagramPageEvent event)
			{
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, new Boolean(diagramPart.isGridVisible()));
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, new Boolean(diagramPart.isGridVisible()));
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING, 
						new Dimension(diagramPart.getGridUnit(), diagramPart.getVerticalGridUnit()));
				markEditorDirty();
			}
			
			@Override
			public void handleGuideStateChangeEvent(final DiagramPageEvent event)
			{
				getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, new Boolean(diagramPart.isShowGuides()));
				markEditorDirty();
			}

			@Override
			public void handleDiagramUpdateEvent(final DiagramPageEvent event)
			{
				refreshPalette();
			}
			
			private boolean bendPointsChangedFromSaved(DiagramConnectionPart connPart)
			{
				boolean changed = false;
				List<org.eclipse.sapphire.ui.Point> newBendPoints = connPart.getConnectionBendpoints();
				List<org.eclipse.sapphire.ui.Point> oldBendPoints = layoutPersistenceService.read(connPart);
				if (newBendPoints.size() != oldBendPoints.size())
				{
					changed = true;
				}
				else
				{
					for (int i = 0; i < newBendPoints.size(); i++)
					{
						if (newBendPoints.get(i).getX() != oldBendPoints.get(i).getX() ||
								newBendPoints.get(i).getY() != oldBendPoints.get(i).getY())
						{
							changed = true;
							break;
						}
					}
				}
				return changed;
			}
			
			private void markEditorForConnectionBendPointEvent(DiagramConnectionPart connPart)
			{
		    	if (layoutPersistenceService.isConnectionPersisted(connPart))
		    	{
		    		if (bendPointsChangedFromSaved(connPart))
		    		{
		    			markEditorDirty();
		    		}
		    		else
		    		{
		    			markEditorClean();
		    		}
		    	}
		    	else
		    	{
		    		markEditorDirty();
		    	}
				
			}
			
		};
		
		this.diagramPart.addListener(this.diagramPartListener);
    }
    
    public IDiagramEditorPageDef getDiagramEditorPageDef()
    {
    	return this.diagramPageDef;
    }
     
    public DiagramLayoutPersistenceService getLayoutPersistenceService()
    {
    	return this.layoutPersistenceService;
    }
    
	@Override
	public boolean isDirty()
	{
		return this.editorIsDirty;
	}
	
	protected void markEditorDirty() {
		this.editorIsDirty = true;
		firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
	}
	
	protected void markEditorClean() {
		this.editorIsDirty = false;
		firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
	}

	protected void removeConnection(DiagramConnectionPart connPart) {
		if (diagramModel == null) {
			return;
		}

		diagramModel.removeConnection(connPart);
		getConfigurationManager().getDiagramRenderingContextCache().remove(connPart);
	}

	private void addConnection(DiagramConnectionPart connPart)
	{
		diagramModel.addConnection(connPart);
		DiagramRenderingContext ctx = new DiagramRenderingContext(connPart, this);
		getConfigurationManager().getDiagramRenderingContextCache().put(connPart, ctx);
	}
	
	protected void addConnectionIfPossible(DiagramConnectionPart connPart) {
		if (diagramModel == null) {
			return;
		}
		IModelElement endpoint1 = connPart.getEndpoint1();
		IModelElement endpoint2 = connPart.getEndpoint2();
		DiagramNodePart nodePart1 = this.diagramPart.getDiagramNodePart(endpoint1);
		DiagramNodePart nodePart2 = this.diagramPart.getDiagramNodePart(endpoint2);
		GraphicalEditPart node1 = getGraphicalEditPart(nodePart1);
		GraphicalEditPart node2 = getGraphicalEditPart(nodePart2);
		if (node1 != null && node2 != null) {
			addConnection(connPart);
		}
	}
	
	private void refreshConnectionNodes(DiagramConnectionPart connPart) {
		if (diagramModel == null) {
			return;
		}
		
		DiagramConnectionModel connectionModel = diagramModel.getDiagramConnectionModel(connPart);
		if (connectionModel != null) {
			connectionModel.handleUpdateConnection();
		}
	}

	protected void updateConnectionEndpoint(DiagramConnectionPart connPart) 
	{
		// Check whether the end points have truely changed		
				
		IModelElement endpoint1 = connPart.getEndpoint1();
		IModelElement endpoint2 = connPart.getEndpoint2();
		DiagramNodePart nodePart1 = this.diagramPart.getDiagramNodePart(endpoint1);
		DiagramNodePart nodePart2 = this.diagramPart.getDiagramNodePart(endpoint2);
		GraphicalEditPart newSrcNode = getGraphicalEditPart(nodePart1);
		GraphicalEditPart newTargetNode = getGraphicalEditPart(nodePart2);

		ConnectionEditPart connEditPart = (ConnectionEditPart)getGraphicalEditPart(connPart);
		GraphicalEditPart oldSrcNode = null;
		GraphicalEditPart oldTargetNode = null;
		if (connEditPart != null)
		{
			oldSrcNode = (GraphicalEditPart)connEditPart.getSource();
			oldTargetNode = (GraphicalEditPart)connEditPart.getTarget();						
		}
		if (oldSrcNode != newSrcNode || oldTargetNode != newTargetNode)
		{
			if (newSrcNode != null && newTargetNode != null)
			{
				removeConnection(connPart);
				addConnection(connPart);				
			}
		}			
		
	}

	protected void updateConnection(DiagramConnectionPart connPart) {
		refreshConnectionNodes(connPart);
	}

	protected void updateConnectionMoveLabel(DiagramConnectionPart connPart) {
		if (diagramModel == null) {
			return;
		}
		
		DiagramConnectionModel connectionModel = diagramModel.getDiagramConnectionModel(connPart);
		if (connectionModel != null) {
			connectionModel.handleUpdateConnectionMoveLabel();
		}
	}

	protected void updateConnectionBendpoint(DiagramConnectionPart connPart) {
		if (diagramModel == null) {
			return;
		}
		
		DiagramConnectionModel connectionModel = diagramModel.getDiagramConnectionModel(connPart);
		if (connectionModel != null) {
			connectionModel.handleUpdateBendPoints();
		}
	}

	protected void moveNode(DiagramNodePart part) {
		if (diagramModel == null) {
			return;
		}
		
		DiagramNodeModel nodeModel = diagramModel.getDiagramNodeModel(part);
		if (nodeModel != null) {
			nodeModel.handleMoveNode();
		}
	}

	protected void removeNode(DiagramNodePart part) {
		if (diagramModel == null) {
			return;
		}
		
		diagramModel.handleRemoveNode(part);
		getConfigurationManager().getDiagramRenderingContextCache().remove(part);
	}

	protected void addNode(DiagramNodePart part) {
		if (diagramModel == null) {
			return;
		}
		
		diagramModel.handleAddNode(part);
		DiagramRenderingContext ctx = new DiagramRenderingContext(part, this);
		getConfigurationManager().getDiagramRenderingContextCache().put(part, ctx);		
	}

	protected void updateNode(DiagramNodePart part) {
		if (diagramModel == null) {
			return;
		}
		
		DiagramNodeModel nodeModel = diagramModel.getDiagramNodeModel(part);
		if (nodeModel != null) {
			nodeModel.handleUpdateNode();
		}
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// If not the active editor, ignore selection changed.
		boolean editorIsActive = getSite().getPage().isPartVisible(this);
		if (!editorIsActive) {
			// Check if we are a page of the active multipage editor
			IEditorPart activeEditor = getSite().getPage().getActiveEditor();
			if (activeEditor != null) {
				// Check if the top level editor if it is active
				editorIsActive = getSite().getPage().isPartVisible(activeEditor);
				if ( activeEditor instanceof FormEditor ) {
                    if ( !( this.equals( ( (FormEditor) activeEditor).getActiveEditor() ) ) ) {
                        editorIsActive = false;
                    }
                }
            }
        }
        if (editorIsActive) 
        {			
        	// TODO figure out sapphire actions in the menu
        	//updateActions(getSelectionActions());
			// Bug 339360 - MultiPage Editor's selectionProvider does not notify PropertySheet (edit) 
			// bypass the selection provider
			if (selection instanceof StructuredSelection) 
			{
				StructuredSelection structuredSelection = (StructuredSelection) selection;
				List<ISapphirePart> partList = new ArrayList<ISapphirePart>();
				List<GraphicalEditPart> editPartList = new ArrayList<GraphicalEditPart>();
				for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) 
				{
					Object object = iterator.next();
					EditPart editPart = null;
					if (object instanceof EditPart) 
					{
						editPart = (EditPart) object;
					}
					else if (object instanceof IAdaptable) 
					{
						editPart = (EditPart) ((IAdaptable) object).getAdapter(EditPart.class);						
					}
					if (editPart != null && editPart.getModel() instanceof DiagramModelBase) {
						SapphirePart sp = ((DiagramModelBase)editPart.getModel()).getSapphirePart();
						partList.add(sp);
						editPartList.add((GraphicalEditPart)editPart);
					}
					getPart().setSelections(partList);
					this.selectedParts = partList;
					this.selectedEditParts = editPartList;
				}
			}
			updateKeyHandler();
		}
	}    
	
	private void updateKeyHandler()
	{
        if (this.diagramKeyHandler != null)
        {
        	this.diagramKeyHandler.dispose();
        	this.diagramKeyHandler = null;
        }
		List<ISapphirePart> selectedParts = this.getSelectedParts();
		this.diagramKeyHandler = new SapphireDiagramKeyHandler(this, selectedParts);
        if (this.graphicalViewerKeyHandler == null)
        {
        	graphicalViewerKeyHandler = new GraphicalViewerKeyHandler(getGraphicalViewer());
        }
		KeyHandler parentKeyHandler = graphicalViewerKeyHandler.setParent(this.diagramKeyHandler);
		getGraphicalViewer().setKeyHandler(parentKeyHandler);
	}
	
	private void refreshPalette() {
		PaletteRoot pr = getPaletteRoot();
		if (pr instanceof SapphirePaletteRoot) {
			SapphirePaletteRoot spr = (SapphirePaletteRoot) pr;
			spr.updatePaletteEntries();
		}
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		if (root == null)
			root = new SapphirePaletteRoot(diagramPart);
		return root;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			this.diagramPart.saveDiagram();
			markEditorClean();
		} catch (Exception e) {
			SapphireUiFrameworkPlugin.log(e);
		}
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		// Is this the right place?
		setEditDomain(new DefaultEditDomain(this));
				
	}

	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		
		GraphicalViewer viewer = getGraphicalViewer();
		
		viewer.getControl().addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				setMouseLocation(e.x, e.y);
			}
		});
		
				
		// set the contents of this editor
		viewer.setContents(diagramModel);
		
		// listen for dropped parts
		viewer.addDropTargetListener(new SapphireTemplateTransferDropTargetListener(this));
		viewer.addDropTargetListener((TransferDropTargetListener) new ObjectsTransferDropTargetListener(viewer));
		postInit();
	}
	
	private void postInit()
	{						
		initRenderingContext();

		// If the layout file doesn't exist or no layout is written to the layout file, apply auto layout
		if (hasNoExistingLayout()) 
		{
			SapphireAction layoutAction = this.diagramPart.getAction("Sapphire.Diagram.Layout");
			if (layoutAction != null) 
			{
				SapphireActionHandler layoutHandler = layoutAction.getFirstActiveHandler();
				if (layoutHandler != null) 
				{
					DiagramRenderingContext context = getConfigurationManager().getDiagramRenderingContextCache().get(this.diagramPart);
					Point pt = getMouseLocation();
					context.setCurrentMouseLocation(pt.x, pt.y);
					layoutHandler.execute(context);
					this.diagramPart.autoLayoutDiagram();
					markEditorClean();
				}
			}
		}
		
	}
	
	private void initRenderingContext()
	{
		// cache DiagramRenderingContext for the diagram edit page part
		DiagramRenderingContext ctx = new DiagramRenderingContext(this.diagramPart, this);
		this.configManager.getDiagramRenderingContextCache().put(this.diagramPart, ctx);
		
		List<DiagramNodeModel> nodes = this.diagramModel.getNodes();
		for (DiagramNodeModel node : nodes)
		{
			ctx = new DiagramRenderingContext(node.getModelPart(), this);
			getConfigurationManager().getDiagramRenderingContextCache().put(node.getModelPart(), ctx);
		}
		List<DiagramConnectionModel> conns = this.diagramModel.getConnections();
		for (DiagramConnectionModel conn : conns)
		{
			ctx = new DiagramRenderingContext(conn.getModelPart(), this);
			getConfigurationManager().getDiagramRenderingContextCache().put(conn.getModelPart(), ctx);
		}
	}
	
	private boolean hasNoExistingLayout()
	{
		if (this.layoutPersistenceService == null)
		{
			return true;
		}
		List<DiagramNodeModel> nodes = this.diagramModel.getNodes();
		for (DiagramNodeModel node : nodes)
		{
			DiagramNodePart nodePart = node.getModelPart();
			Bounds bounds = nodePart.getNodeBounds();
			if (bounds.getX() == -1 || bounds.getY() == -1)
			{
				return true;
			}
		}
				
		return false;
	}
	
	public DiagramModel getDiagramModel() {
		return this.diagramModel;
	}
	
	public DiagramConfigurationManager getConfigurationManager()
	{
		return this.configManager;
	}
	
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();		
		
		viewer.setEditPartFactory(new SapphireDiagramEditorEditPartFactory(getConfigurationManager()));
		
		viewer.setRootEditPart(new ScalableFreeformRootEditPart()
		{
			@Override
			protected GridLayer createGridLayer() 
			{
				return new SapphireDiagramGridLayer(diagramModel);
			}			
		});
		
		// configure the context menu provider
		ContextMenuProvider cmProvider = new SapphireDiagramEditorContextMenuProvider(this);
		viewer.setContextMenu(cmProvider);
		
		// Configure grid and guide properties
		boolean isGridVisibleInViewer = false;
		if (viewer.getProperty(SnapToGrid.PROPERTY_GRID_VISIBLE) != null)
		{
			isGridVisibleInViewer = (Boolean) viewer.getProperty(SnapToGrid.PROPERTY_GRID_VISIBLE);
		}
		if (this.diagramPart.isGridVisible() != isGridVisibleInViewer)
		{
			viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, this.diagramPart.isGridVisible());
			viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, this.diagramPart.isGridVisible());
			viewer.setProperty(SnapToGrid.PROPERTY_GRID_SPACING, 
					new Dimension(this.diagramPart.getGridUnit(), this.diagramPart.getVerticalGridUnit()));
		}
		
		boolean isShowGuidesInViewer = false;
		if (viewer.getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED) != null)
		{
			isShowGuidesInViewer = (Boolean)viewer.getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
		}
		if (this.diagramPart.isShowGuides() != isShowGuidesInViewer)
		{
			viewer.setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, this.diagramPart.isShowGuides());
		}
		
		// Support context help
		
		this.getGraphicalControl().addHelpListener(new HelpListener() 
        {
            public void helpRequested(HelpEvent event) 
            {            	
            	if (getSelectedParts() != null && getSelectedParts().size() == 1)
            	{
            		ISapphirePart part = getSelectedParts().get(0);
	            	final SapphireHelpContext context = new SapphireHelpContext(part.getLocalModelElement(), null);
	            	if (context.getText() != null || (context.getRelatedTopics() != null && context.getRelatedTopics().length > 0))
	            	{
		                // determine a location in the upper right corner of the widget
		                org.eclipse.swt.graphics.Point point = SapphireHelpSystem.computePopUpLocation(event.widget.getDisplay());
		                // display the help
		                PlatformUI.getWorkbench().getHelpSystem().displayContext(context, point.x, point.y);
	            	}
            	}
            }
        });
		
	}
			
	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() 
	{
		return new PaletteViewerProvider(getEditDomain()) 
		{
			@Override
			protected void configurePaletteViewer(PaletteViewer viewer)
			{
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}
	

	public SapphireDiagramEditorPagePart getPart() {
		return this.diagramPart;
	}
	
	@Override
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	public List<ISapphirePart> getSelectedParts()
	{
		return this.selectedParts;
	}
	
	public List<GraphicalEditPart> getSelectedEditParts()
	{
		return this.selectedEditParts;
	}
	
	public GraphicalEditPart getGraphicalEditPart(ISapphirePart sapphirePart)
	{
		if (sapphirePart instanceof DiagramNodePart || sapphirePart instanceof DiagramConnectionPart)
		{
			GraphicalViewer viewer = this.getGraphicalViewer();
			
			Object editpartObj = null;
			DiagramNodePart nodePart = null;
			DiagramConnectionPart connPart = null;
			if (sapphirePart instanceof DiagramNodePart)
			{
				nodePart = (DiagramNodePart)sapphirePart;
				DiagramNodeModel nodeModel = this.getDiagramModel().getDiagramNodeModel(nodePart);
				editpartObj = viewer.getEditPartRegistry().get(nodeModel);
			}
			else if (sapphirePart instanceof DiagramConnectionPart)
			{
				connPart = (DiagramConnectionPart)sapphirePart;
				DiagramConnectionModel connModel = this.getDiagramModel().getDiagramConnectionModel(connPart);
				editpartObj = viewer.getEditPartRegistry().get(connModel);				
			}
			return (GraphicalEditPart)editpartObj;
		}
		return null;
	}
	
	public void selectAndDirectEditPart(ISapphirePart part)
	{
		if (part instanceof DiagramNodePart || part instanceof DiagramConnectionPart)
		{
			GraphicalViewer viewer = this.getGraphicalViewer();
			viewer.getControl().forceFocus();
			
			GraphicalEditPart editpart = getGraphicalEditPart(part);
			if (editpart != null) 
			{
				// Force a layout first.
				viewer.flush();
				viewer.select(editpart);
				if (part instanceof DiagramNodePart)
				{
					this.getDiagramModel().handleDirectEditing((DiagramNodePart)part);
				}
				else if (part instanceof DiagramConnectionPart)
				{
					this.getDiagramModel().handleDirectEditing((DiagramConnectionPart)part);
				}
				
			}
		}
	}
	
	public void selectAll()
	{
		GraphicalViewer viewer = this.getGraphicalViewer();
		for (Object obj : viewer.getEditPartRegistry().values())
		{
			EditPart editPart = (EditPart)obj;
			viewer.appendSelection(editPart);
		}
	}
	
	public void selectAllNodes()
	{
		GraphicalViewer viewer = this.getGraphicalViewer();
		for (Object obj : viewer.getEditPartRegistry().values())
		{
			if (obj instanceof DiagramNodeEditPart)
			{
				viewer.appendSelection((DiagramNodeEditPart)obj);
			}
		}
	}

	public Point getMouseLocation() {
		if (mouseLocation == null) {
			mouseLocation = new Point();
		}
		return mouseLocation;
	}

	private void setMouseLocation(int x, int y) 
	{
		getMouseLocation().setLocation(x, y);
		Point realLocation = calculateRealMouseLocation(getMouseLocation());
		this.diagramPart.setMouseLocation(realLocation.x, realLocation.y);
	}
	
	public FigureCanvas getFigureCanvas() 
	{
		GraphicalViewer viewer = getGraphicalViewer();
		return (FigureCanvas) viewer.getControl();
	}
	
	/**
	 * Calculates the location in dependence from scrollbars and zoom factor.
	 * 
	 * @param nativeLocation
	 *            the native location
	 * @return the point
	 */
	public Point calculateRealMouseLocation(Point nativeLocation) 
	{

		Point ret = new Point(nativeLocation);
		Point viewLocation;
		// view location depends on the current scroll bar position
		viewLocation = getFigureCanvas().getViewport().getViewLocation();

		ret.x += viewLocation.x;
		ret.y += viewLocation.y;

		ZoomManager zoomManager = (ZoomManager) getGraphicalViewer().getProperty(ZoomManager.class.toString());
		if (zoomManager != null)
		{
			ret = ret.getScaled(1 / zoomManager.getZoom());
		}

		return ret;
	}
	

	@Override
	public void dispose() {
		super.dispose();
		
		diagramModel.dispose();
		diagramPart.dispose();
		diagramPart.removeListener(diagramPartListener);
		if (layoutPersistenceService != null)
		{
			layoutPersistenceService.dispose();
		}
	}
		
}