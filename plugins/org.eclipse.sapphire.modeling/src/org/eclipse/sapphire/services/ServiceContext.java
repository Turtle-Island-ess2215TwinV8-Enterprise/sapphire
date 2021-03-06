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

package org.eclipse.sapphire.services;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.sapphire.ListenerContext;
import org.eclipse.sapphire.modeling.internal.SapphireModelingExtensionSystem;
import org.eclipse.sapphire.modeling.internal.SapphireModelingExtensionSystem.ServiceExtension;
import org.eclipse.sapphire.modeling.util.DependencySorter;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class ServiceContext
{
    public static final String ID_ROOT = "Sapphire";
    public static final String ID_ELEMENT_INSTANCE = "Sapphire.Element.Instance";
    public static final String ID_ELEMENT_METAMODEL = "Sapphire.Element.MetaModel";
    public static final String ID_PROPERTY_INSTANCE = "Sapphire.Property.Instance";
    public static final String ID_PROPERTY_METAMODEL = "Sapphire.Property.MetaModel";
    
    private final String type;
    private final ServiceContext parent;
    private List<ServiceProxy> services;
    private ListenerContext coordinatingListenerContext;
    private boolean disposed = false;
    
    public ServiceContext( final String type,
                           final ServiceContext parent )
    {
        this.type = type;
        this.parent = parent;
    }

    public final String type()
    {
        return this.type;
    }
    
    public final ServiceContext parent()
    {
        return this.parent;
    }
    
    public <T> T find( final Class<T> type )
    {
        return null;
    }
    
    public final <S extends Service> S service( final Class<S> type )
    {
        if( type == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( this.disposed )
        {
            throw new IllegalStateException();
        }
        
        final List<S> services = services( type );
        return ( services.isEmpty() ? null : services.get( 0 ) );
    }

    public final synchronized <S extends Service> List<S> services( final Class<S> type )
    {
        if( type == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( this.disposed )
        {
            throw new IllegalStateException();
        }
        
        if( this.services == null )
        {
            final ListFactory<ServiceProxy> services = ListFactory.start();
            
            services.add( local() );
            
            for( final ServiceExtension extension : SapphireModelingExtensionSystem.services() )
            {
                if( extension.contexts().contains( this.type ) )
                {
                    services.add
                    (
                        new ServiceProxy
                        (
                            this,
                            extension.id(),
                            extension.implementation(),
                            extension.condition(),
                            extension.overrides(),
                            null
                        )
                    );
                }
            }
            
            this.services = new CopyOnWriteArrayList<ServiceProxy>( services.result() );
        }
        
        final DependencySorter<String,S> sorter = new DependencySorter<String,S>();
        final ListFactory<ServiceProxy> failed = ListFactory.start();
        
        for( final ServiceProxy proxy : this.services )
        {
            if( type.isAssignableFrom( proxy.type() ) )
            {
                if( sorter.contains( proxy.id() ) )
                {
                    failed.add( proxy );
                }
                else
                {
                    final S service = type.cast( proxy.service() );
                    
                    if( service == null )
                    {
                        failed.add( proxy );
                    }
                    else
                    {
                        if( this.coordinatingListenerContext != null )
                        {
                            service.coordinate( this.coordinatingListenerContext );
                        }
                        
                        sorter.add( service.id(), service );
                        
                        for( final String override : service.overrides() )
                        {
                            sorter.dependency( override, service.id() );
                        }
                    }
                }
            }
        }
        
        this.services.removeAll( failed.result() );
        
        if( this.parent != null )
        {
            for( final S service : this.parent.services( type ) )
            {
                if( ! sorter.contains( service.id()  ) )
                {
                    sorter.add( service.id(), service );
                    
                    for( final String override : service.overrides() )
                    {
                        sorter.dependency( override, service.id() );
                    }
                }
            }
        }
        
        final List<S> services = sorter.sort();
        
        for( final Service service : services )
        {
            service.initIfNecessary();
        }
        
        return services;
    }
    
    public final void coordinate( final ListenerContext context )
    {
        this.coordinatingListenerContext = context;
    }

    protected List<ServiceProxy> local()
    {
        return Collections.emptyList();
    }
    
    public final void dispose()
    {
        this.disposed = true;
        
        for( final ServiceProxy service : this.services )
        {
            service.dispose();
        }
        
        this.services = null;
    }
    
}
