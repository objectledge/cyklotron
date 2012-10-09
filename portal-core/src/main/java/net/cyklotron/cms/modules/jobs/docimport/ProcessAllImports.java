package net.cyklotron.cms.modules.jobs.docimport;

import org.objectledge.scheduler.Job;

import net.cyklotron.cms.docimport.DocumentImportCoordinatorService;

public class ProcessAllImports
    extends Job
{
    private final DocumentImportCoordinatorService documentImportService;

    public ProcessAllImports(DocumentImportCoordinatorService documentImportService)
    {
        this.documentImportService = documentImportService;
    }

    @Override
    public void run(String[] arguments)
    {
        documentImportService.processAllImports();
    }
}
