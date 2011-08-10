// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 

package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.ngodatabase.organizations.IncomingOrganizationsService;
import net.cyklotron.cms.ngodatabase.organizations.OrganizationNewsFeedService;
import net.cyklotron.cms.ngodatabase.organizations.OrganizationsIndex;
import net.cyklotron.cms.ngodatabase.organizations.OutgoingOrganizationsService;
import net.cyklotron.cms.ngodatabase.organizations.UpdatedDocumentsProvider;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.util.OfflineLinkRenderingService;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.database.Database;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.utils.StringUtils;
import org.picocontainer.Startable;

public class NgoDatabaseServiceImpl
    implements NgoDatabaseService, Startable
{
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private static final String DEFAULT_LOCALE = "pl_PL";

    private final OrganizationsIndex organizationsIndex;

    private final IncomingOrganizationsService incoming;

    private final OutgoingOrganizationsService outgoing;

    private final OrganizationNewsFeedService newsFeed;
    
    private final UpdatedDocumentsProvider updatedDocumetns;

    private final DateFormat dateFormat;

    private final Locale locale;

    public NgoDatabaseServiceImpl(Configuration config, Logger logger,
        UpdatedDocumentsProvider updatedDocuments, FileSystem fileSystem, SiteService siteService,
        CoralSessionFactory coralSessionFactory, Database database, Templating templating,
        OfflineLinkRenderingService offlineLinkRenderingService, DateFormatter dateFormatter,
        CategoryService categoryService)
        throws Exception
    {
        // date format
        Configuration dateFormatConfig = config.getChild("dateFormat");
        this.dateFormat = new SimpleDateFormat(dateFormatConfig.getChild("pattern").getValue(
            DEFAULT_DATE_FORMAT));
        this.locale = StringUtils.getLocale(dateFormatConfig.getChild("locale").getValue(
            DEFAULT_LOCALE));

        // lucene index
        this.organizationsIndex = new OrganizationsIndex(fileSystem, logger);

        // updated documents provider
        this.updatedDocumetns = updatedDocuments;

        // service components
        this.incoming = new IncomingOrganizationsService(config.getChild("incoming"), fileSystem,
            organizationsIndex, logger);
        this.outgoing = new OutgoingOrganizationsService(config.getChild("outgoing"), updatedDocumetns,
            coralSessionFactory, fileSystem, logger, dateFormat);
        this.newsFeed = new OrganizationNewsFeedService(config.getChild("newsFeed"), dateFormat,
            locale, organizationsIndex, updatedDocumetns, categoryService, coralSessionFactory,
            fileSystem, dateFormatter, offlineLinkRenderingService, templating, logger);
    }

    @Override
    public void updateIncoming()
    {
        incoming.readIncoming(true);
    }

    @Override
    public void start()
    {
        incoming.readIncoming(false);
    }

    @Override
    public void stop()
    {

    }

    // incoming organization data

    @Override
    public Organization getOrganization(long id)
    {
        return organizationsIndex.getOrganization(id);
    }

    @Override
    public List<Organization> getOrganizations(String substring)
    {
        return organizationsIndex.getOrganizations(substring);
    }

    // outgoing organization data

    @Override
    public void updateOutgoing()
    {
        outgoing.updateOutgoing();
    }

    @Override
    public void updateOutgoing(Date startDate, Date endDate, OutputStream outputStream)
        throws IOException
    {
        outgoing.updateOutgoing(startDate, endDate, outputStream);
    }    
    
    // news feeds

    @Override
    public String getOrganizationNewsFeed(Parameters parameters)
        throws ProcessingException
    {
        return newsFeed.getOrganizationNewsFeed(parameters);
    }

}
