package net.cyklotron.cms.catalogue;

import java.util.List;
import java.util.Set;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;

public class ProblemReportItem
{
    private final Resource resource;

    private final List<DocumentNodeResource> descriptionDocCandidates;

    private final List<FileResource> downloads;

    private final Set<Problem> problems;
    
    private final Set<IndexCard.Property> missingProperties;

    public ProblemReportItem(Resource resource,
        List<DocumentNodeResource> descriptionDocCandidates, List<FileResource> downloads,
        Set<Problem> problems, Set<IndexCard.Property> missingProperties)
    {
        this.resource = resource;
        this.descriptionDocCandidates = descriptionDocCandidates;
        this.downloads = downloads;
        this.problems = problems;
        this.missingProperties = missingProperties;
    }

    public Resource getResource()
    {
        return resource;
    }

    public List<DocumentNodeResource> getDescriptionDocCandidates()
    {
        return descriptionDocCandidates;
    }

    public List<FileResource> getDownloads()
    {
        return downloads;
    }

    public Set<Problem> getProblems()
    {
        return problems;
    }
    
    public Set<IndexCard.Property> getMissingProperties()
    {
        return missingProperties;
    }
}
