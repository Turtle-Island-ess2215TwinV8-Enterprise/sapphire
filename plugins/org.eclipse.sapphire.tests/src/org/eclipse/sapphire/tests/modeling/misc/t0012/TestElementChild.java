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

package org.eclipse.sapphire.tests.modeling.misc.t0012;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface TestElementChild extends Element
{
    ElementType TYPE = new ElementType( TestElementChild.class );
    
    // *** ValueProperty1 ***
    
    ValueProperty PROP_VALUE_PROPERTY_1 = new ValueProperty( TYPE, "ValueProperty1" );
    
    Value<String> getValueProperty1();
    void setValueProperty1( String value );

}