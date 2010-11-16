package org.eclipse.sapphire.doc.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.SapphireBrowseActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerFactoryDef;

public class KeyFolderBrowseHandlerFactory extends SapphireActionHandlerFactory
{
    private File folder;
    
    @Override
    public void init( SapphireAction action, ISapphireActionHandlerFactoryDef def )
    {
        super.init( action, def );
        this.folder = new File( def.getParam( "folder" ) );
    }

    @Override
    public List<SapphireActionHandler> create()
    {
        List<SapphireActionHandler> handlers = new ArrayList<SapphireActionHandler>();
        
        for( File file : this.folder.listFiles() )
        {
            if( file.isFile() )
            {
                handlers.add( new Handler( file ) );
            }
        }
        
        return handlers;
    }
    
    private static class Handler extends SapphireBrowseActionHandler
    {
        private final File file;
        
        public Handler( File file )
        {
            this.file = file;
            setLabel( this.file.getName() );
        }

        @Override
        protected String browse( SapphireRenderingContext context )
        {
            return this.file.getAbsolutePath();
        }
    }
}
