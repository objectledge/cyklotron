/**
 * 
 */
package net.cyklotron.cms.modules.views.documents;

import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.structure.internal.ProposedDocumentData;

public class DocumentStateTool
{
    private final CoralSession coralSession;

    public DocumentStateTool(CoralSession coralSession)
    {
        this.coralSession = coralSession;            
    }
    
    public String getState(DocumentNodeResource doc)
    {
        ProposedDocumentData data = new ProposedDocumentData();
        if(doc.isProposedContentDefined())
        {
            data.fromProposal(doc, coralSession);
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