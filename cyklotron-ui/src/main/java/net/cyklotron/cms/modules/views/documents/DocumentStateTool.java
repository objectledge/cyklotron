/**
 * 
 */
package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.structure.internal.ProposedDocumentData;

public class DocumentStateTool
{
    private final CoralSession coralSession;
    
    protected Logger logger;

    public DocumentStateTool(CoralSession coralSession, Logger logger)
    {
        this.coralSession = coralSession;
        this.logger = logger;
    }
    
    public String getState(DocumentNodeResource doc)
    {
        ProposedDocumentData data = new ProposedDocumentData(logger);
        if(doc.isProposedContentDefined())
        {
            try
            {
                data.fromProposal(doc, coralSession);
            }
            catch(Exception e)
            {
                return "DAMAEGED";
            }
            if(data.isRemovalRequested())
            {
                return "REMOVE_REQUEST";
            }
            else
            {
                return "UPDATE_REQUEST";
            }
        }
        else if(doc.getState() == null || doc.getState().getName().equals("published"))
        {
            return "PUBLISHED";
        }
        else if(doc.getState().getName().equals("rejected"))
        {
            return "REJECTED";
        }
        else
        {
            return "PENDING";
        }
    }

    public Boolean isPublished(DocumentNodeResource doc)
    {
        if(doc.getState() == null || doc.getState().getName().equals("published"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}