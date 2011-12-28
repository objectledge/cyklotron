package net.cyklotron.cms.docimport;

import org.objectledge.coral.session.CoralSession;

public interface DocumentImportCoordinatorService
{
    void processImport(ImportResource importResource, CoralSession coralSession);

    void processAllImports();
}
