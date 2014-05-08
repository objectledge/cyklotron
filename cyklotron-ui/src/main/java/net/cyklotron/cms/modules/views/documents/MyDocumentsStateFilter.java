package net.cyklotron.cms.modules.views.documents;

import java.util.Arrays;
import java.util.List;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.structure.table.StateFilter;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;

/**
 * DocumentNodeResource state filter
 * 
 * @author lukasz
 */
public class MyDocumentsStateFilter
    extends StateFilter<DocumentNodeResource>
{
    private final DocumentStateTool documentStateTool;

    private final List<String> myDocumentAllowedStateName = Arrays
        .asList(DocumentStateTool.DOCUMENT_STATES);

    public MyDocumentsStateFilter(CoralSession coralSession, Logger logger, String[] states)
    {
        super(states);
        this.allowedStatesNames.retainAll(myDocumentAllowedStateName);
        this.documentStateTool = new DocumentStateTool(coralSession, logger);
    }

    public boolean accept(DocumentNodeResource doc)
    {
        return allowedStatesNames.contains(documentStateTool.getState(doc));
    }
}
