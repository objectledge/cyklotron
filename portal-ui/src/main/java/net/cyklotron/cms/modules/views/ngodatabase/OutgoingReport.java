package net.cyklotron.cms.modules.views.ngodatabase;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.cyklotron.cms.ngodatabase.NgoDatabaseService;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.DefaultBuilder;
import org.objectledge.web.mvc.builders.EnclosingView;
import org.objectledge.web.mvc.security.SecurityChecking;

public class OutgoingReport
    extends DefaultBuilder
    implements SecurityChecking
{
    private final NgoDatabaseService ngoDatabaseService;

    public OutgoingReport(Context context, NgoDatabaseService ngoDatabaseService)
    {
        super(context);
        this.ngoDatabaseService = ngoDatabaseService;
    }

    @Override
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        try
        {
            Parameters parameters = context.getAttribute(RequestParameters.class);
            HttpContext httpContext = context.getAttribute(HttpContext.class);
            httpContext.setContentType("text/xml");
            httpContext.getResponse().addHeader("Content-Disposition",
                "attachment; filename=export.xml");
            OutputStream os = httpContext.getOutputStream();
            String start = parameters.get("start");
            String end = parameters.get("end");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = df.parse(start);
            Date endDate = df.parse(end);
            ngoDatabaseService.updateOutgoing(startDate, endDate, os);
            return null;
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }

    @Override
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return EnclosingView.TOP;
    }

    @Override
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {        
        return false;
    }

    @Override
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return true;
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
