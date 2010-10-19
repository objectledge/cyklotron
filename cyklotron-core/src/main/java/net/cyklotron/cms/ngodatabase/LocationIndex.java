package net.cyklotron.cms.ngodatabase;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

public class LocationIndex extends AbstractIndex<Location>
{
    private static final String INDEX_PATH = "ngo/locations/index";
    
    public LocationIndex(FileSystem fileSystem, Logger logger)
        throws IOException
    {
        super(fileSystem, logger, INDEX_PATH);        
    }

    @Override
    protected Document toDocument(Location item)
    {
        Document document = new Document();
        document.add(new Field("province", item.getProvince(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("city", item.getCity(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("street", item.getStreet(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("postCode", item.getPostCode(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        return document;
    }

    @Override
    protected Location fromDocument(Document doc)
    {
        String province = doc.get("province");
        String city = doc.get("city");
        String street = doc.get("street");
        String postCode = doc.get("postCode");
        return new Location(province, city, street, postCode);
    }
}
