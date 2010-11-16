/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.modeling.xml;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

@XmlRootBinding( namespace = "http://xmlns.oracle.com/eclipse/tools/sapphire/tests/xml-binding",
                 schemaLocation = "http://xmlns.oracle.com/eclipse/tools/sapphire/tests/xml-binding/1.0",
                 elementName = "test-root" )

public interface IXmlBindingTestModelAltB

    extends IXmlBindingTestModel
    
{
    ModelElementType TYPE = new ModelElementType( IXmlBindingTestModelAltB.class );

}
