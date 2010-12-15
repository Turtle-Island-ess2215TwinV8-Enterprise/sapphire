package org.eclipse.sapphire.doc.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.help.IHelpContentProducer;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.UrlResourceStore;
import org.eclipse.sapphire.modeling.extensibility.IFunctionDef;
import org.eclipse.sapphire.modeling.internal.SapphireModelingExtensionSystem;
import org.eclipse.sapphire.modeling.internal.SapphireModelingExtensionSystem.BundleExtensionHandle;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.sdk.IExtensionSummaryExportOp;
import org.eclipse.sapphire.sdk.IExtensionSummarySectionColumnDef;
import org.eclipse.sapphire.sdk.IExtensionSummarySectionDef;
import org.eclipse.sapphire.sdk.ISapphireExtensionDef;
import org.eclipse.sapphire.ui.IExportModelDocumentationOp;
import org.eclipse.sapphire.ui.def.ISapphireActionDef;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerFactoryDef;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerFilterDef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;

public class DynamicContentProducer implements IHelpContentProducer
{
    private List<ISapphireExtensionDef> extensions;
    
    public InputStream getInputStream( final String pluginID,
                                       final String href,
                                       final Locale locale )
    {
        String content = null;
        
        if( pluginID.equals( "org.eclipse.sapphire.doc" ) )
        {
            if( href.equals( "html/extensions/existing.html" ) )
            {
                final IExtensionSummaryExportOp op = IExtensionSummaryExportOp.TYPE.instantiate();
                op.setDocumentBodyTitle( "Sapphire Extensions" );
                
                content = op.execute( getExtensions(), new NullProgressMonitor() );
            }
            if( href.equals( "html/el/index.html" ) )
            {
                final IExtensionSummaryExportOp op = IExtensionSummaryExportOp.TYPE.instantiate();
                op.setCreateFinishedDocument( false );
                
                final IExtensionSummarySectionDef section = op.getSections().addNewElement();
                section.setExtensionType( ISapphireExtensionDef.PROP_FUNCTIONS.getName() );
                section.setIncludeSectionHeader( false );
                
                final IExtensionSummarySectionColumnDef nameColumn = section.getColumns().addNewElement();
                nameColumn.setName( IFunctionDef.PROP_NAME.getName() );
                
                final IExtensionSummarySectionColumnDef descColumn = section.getColumns().addNewElement();
                descColumn.setName( IFunctionDef.PROP_DESCRIPTION.getName() );
                
                final String functions = op.execute( getExtensions(), new NullProgressMonitor() );

                content = loadResource( "html/el/index.html" );
                content = content.replace( "##functions##", functions );
            }
            else if( href.equals( "html/ui/actions/index.html" ) )
            {
                content = loadResource( "html/ui/actions/index.html" );
                
                final String docAction = exportModelDocumentation( ISapphireActionDef.TYPE );
                content = content.replace( "##action-details##", docAction );
                
                final String docActionHandler = exportModelDocumentation( ISapphireActionHandlerDef.TYPE );
                content = content.replace( "##action-handler-details##", docActionHandler );
                
                final String docActionHandlerFactory = exportModelDocumentation( ISapphireActionHandlerFactoryDef.TYPE );
                content = content.replace( "##action-handler-factory-details##", docActionHandlerFactory );
                
                final String docActionHandlerFilter = exportModelDocumentation( ISapphireActionHandlerFilterDef.TYPE );
                content = content.replace( "##action-handler-filter-details##", docActionHandlerFilter );
            }
        }
        
        if( content != null )
        {
            return new ByteArrayInputStream( content.getBytes() );
        }

        return null;
    }
    
    private static String exportModelDocumentation( final ModelElementType type )
    {
        final IExportModelDocumentationOp op = IExportModelDocumentationOp.TYPE.instantiate();
        return op.execute( type, new NullProgressMonitor() );
    }
    
    private static String loadResource( final String name )
    {
        final InputStream in = DynamicContentProducer.class.getClassLoader().getResourceAsStream( name );
        
        if( in == null )
        {
            throw new IllegalArgumentException( name );
        }
        
        try
        {
            final BufferedReader r = new BufferedReader( new InputStreamReader( in ) );
            final char[] chars = new char[ 1024 ];
            final StringBuilder buf = new StringBuilder();
            
            for( int i = r.read( chars ); i != -1; i = r.read( chars ) )
            {
                buf.append( chars, 0, i );
            }
            
            return buf.toString();
        }
        catch( IOException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            try
            {
                in.close();
            }
            catch( IOException e ) {}
        }
    }
    
    private synchronized List<ISapphireExtensionDef> getExtensions()
    {
        if( extensions == null )
        {
            final List<ISapphireExtensionDef> list = new ArrayList<ISapphireExtensionDef>();
            
            for( BundleExtensionHandle handle : SapphireModelingExtensionSystem.getBundleExtensionHandles() )
            {
                for( URL url : handle.findExtensionFiles() )
                {
                    try
                    {
                        final XmlResourceStore store = new XmlResourceStore( new UrlResourceStore( url ) );
                        final RootXmlResource resource = new RootXmlResource( store );
                        final ISapphireExtensionDef extension = ISapphireExtensionDef.TYPE.instantiate( resource );
                        list.add( extension );
                    }
                    catch( ResourceStoreException e )
                    {
                        SapphireUiFrameworkPlugin.log( e );
                    }
                }
            }
            
            extensions = Collections.unmodifiableList( list );
        }
        
        return extensions;
    }
    
}