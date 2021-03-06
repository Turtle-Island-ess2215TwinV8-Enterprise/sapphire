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

package org.eclipse.sapphire.tests.modeling.el.properties.thisp;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests This property.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ThisPropertyTests extends TestExpr
{
    private ThisPropertyTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "ThisPropertyTests" );

        suite.addTest( new ThisPropertyTests( "testThisProperty" ) );
        
        return suite;
    }
    
    public void testThisProperty()
    {
        final Element element = Element.TYPE.instantiate();
        
        try
        {
            final FunctionContext context = new ModelElementFunctionContext( element );
            final FunctionResult fr = ExpressionLanguageParser.parse( "${ This }" ).evaluate( context );
            
            try
            {
                assertSame( element, fr.value() );
            }
            finally
            {
                fr.dispose();
            }
        }
        finally
        {
            element.dispose();
        }
    }

}
