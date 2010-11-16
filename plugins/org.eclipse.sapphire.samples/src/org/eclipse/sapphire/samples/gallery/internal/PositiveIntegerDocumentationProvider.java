package org.eclipse.sapphire.samples.gallery.internal;

import org.eclipse.help.IHelpResource;
import org.eclipse.sapphire.modeling.annotations.DocumentationData;
import org.eclipse.sapphire.modeling.annotations.DocumentationProviderImpl;

public class PositiveIntegerDocumentationProvider extends DocumentationProviderImpl {

    @Override
    public DocumentationData getDocumentationData() {
        return new DocumentationData() {

            @Override
            public String getContent() {
                return "Positive integers are all the whole numbers greater than zero: 1, 2, 3, 4, 5, ......";
            }

            @Override
            public IHelpResource[] getTopics() {
                IHelpResource topics[] = new IHelpResource[1];
                topics[0] = new IHelpResource() {
        
                    public String getHref() {
                        return "http://en.wikipedia.org/wiki/Positive_number";
                    }
        
                    public String getLabel() {
                        return "wikipedia positive number";
                    }
                    
                };
                return topics;
            }
        };
    }

}
