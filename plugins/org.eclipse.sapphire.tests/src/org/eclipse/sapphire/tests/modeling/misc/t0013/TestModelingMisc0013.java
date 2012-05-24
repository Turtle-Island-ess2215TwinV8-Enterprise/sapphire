/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.modeling.misc.t0013;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests use of @DependsOn where the property specifying the dependency resides in an implied element property
 * and the specified dependency path traverses through this implied element property.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestModelingMisc0013 extends SapphireTestCase
{
    private TestModelingMisc0013( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestModelingMisc0013" );

        suite.addTest( new TestModelingMisc0013( "test" ) );
        
        return suite;
    }
    
    public void test() throws Exception
    {
        final RootElement root = RootElement.TYPE.instantiate();
        root.getChild();
    }

}
