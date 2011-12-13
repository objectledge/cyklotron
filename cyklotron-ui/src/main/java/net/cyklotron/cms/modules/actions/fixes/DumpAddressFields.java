package net.cyklotron.cms.modules.actions.fixes;

import java.io.PrintWriter;

import org.dom4j.Document;
import org.objectledge.context.Context;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;

import net.cyklotron.cms.documents.DocumentMetadataHelper;
import net.cyklotron.cms.documents.DocumentNodeResource;

public class DumpAddressFields
    implements Valve
{
    private final CoralSessionFactory coralSessionFactory;

    private final FileSystem fileSystem;

    public DumpAddressFields(CoralSessionFactory coralSessionFactory, FileSystem fileSystem)
    {
        this.coralSessionFactory = coralSessionFactory;
        this.fileSystem = fileSystem;
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        try
        {
            CoralSession coralSession = coralSessionFactory.getCurrentSession();
            QueryResults results;
            try
            {
                results = coralSession.getQuery().executeQuery(
                    "FIND RESOURCE FROM documents.document_node");
            }
            catch(MalformedQueryException e)
            {
                throw new ProcessingException("cannot get 'documents.document_node' resources", e);
            }
            System.out.println("got results");
            PrintWriter pw = new PrintWriter(fileSystem.getWriter("addresses.txt", "UTF-8"));
            for(QueryResults.Row row : results)
            {
                DocumentNodeResource node = (DocumentNodeResource)row.get();
                if(node.getMeta() != null && !node.getMeta().trim().isEmpty())
                {
                    Document metaDom = DocumentMetadataHelper.textToDom4j(node.getMeta().replace(
                        "&", ""));
                    String organisationAddress = DocumentMetadataHelper.selectFirstText(metaDom,
                        "/meta/organisation/address");
                    pw.print(organisationAddress.replace("\n", "").replace("\r", "") + "\n");
                }
            }
            pw.close();
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }
}
