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

package org.eclipse.sapphire.tests.modeling.el.functions.content;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests Content function.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ContentFunctionTests extends TestExpr
{
    private ContentFunctionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "ContentFunctionTests" );

        suite.addTest( new ContentFunctionTests( "testContentFunction" ) );
        suite.addTest( new ContentFunctionTests( "testContentFunctionNull" ) );
        suite.addTest( new ContentFunctionTests( "testContentFunctionWrongType" ) );
        
        return suite;
    }
    
    public void testContentFunction()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        FunctionResult fr = ExpressionLanguageParser.parse( "${ IntegerValue.Content }" ).evaluate( context );
        
        try
        {
            assertNull( fr.value() );
            
            element.setIntegerValue( 3 );
            assertEquals( 3, fr.value() );

            element.setIntegerValue( "abc" );
            assertNull( fr.value() );
        }
        finally
        {
            fr.dispose();
        }

        fr = ExpressionLanguageParser.parse( "${ IntegerValueWithDefault.Content }" ).evaluate( context );
        
        try
        {
            assertEquals( 1, fr.value() );
            
            element.setIntegerValueWithDefault( 3 );
            assertEquals( 3, fr.value() );

            element.setIntegerValueWithDefault( "abc" );
            assertEquals( 1, fr.value() );
        }
        finally
        {
            fr.dispose();
        }

        fr = ExpressionLanguageParser.parse( "${ Transient.Content }" ).evaluate( context );
        
        try
        {
            assertNull( fr.value() );
            
            element.setTransient( this );
            assertSame( this, fr.value() );
        }
        finally
        {
            fr.dispose();
        }
    }

    public void testContentFunctionNull()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        final FunctionResult fr = ExpressionLanguageParser.parse( "${ Content( null ) }" ).evaluate( context );
        
        try
        {
            final Status st = fr.status();
            
            assertEquals( Status.Severity.ERROR, st.severity() );
            assertEquals( "Function Content does not accept nulls in position 0.", st.message() );
        }
        finally
        {
            fr.dispose();
        }
    }

    public void testContentFunctionWrongType()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        final FunctionResult fr = ExpressionLanguageParser.parse( "${ Content( 'abc' ) }" ).evaluate( context );
        
        try
        {
            final Status st = fr.status();
            
            assertEquals( Status.Severity.ERROR, st.severity() );
            assertEquals( "Function Content expects org.eclipse.sapphire.Property in position 0, but java.lang.String was found. A conversion was not possible.", st.message() );
        }
        finally
        {
            fr.dispose();
        }
    }

}
