package net.cyklotron.cms.modules.actions;

import net.cyklotron.cms.management.ErrorReportingService;

import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;

public class ReportError
    implements Valve
{
    private final ErrorReportingService errorReportingService;

    public ReportError(ErrorReportingService errorReportingService)
    {
        this.errorReportingService = errorReportingService;
    }

    public void process(Context context)
        throws ProcessingException
    {
        RequestParameters parameters = context.getAttribute(RequestParameters.class);

        String requestMarker = parameters.get("requestMarker", "");
        String time = parameters.get("time", "");
        String url = parameters.get("url", "");
        String stackTrace = parameters.get("stackTrace", "");
        String additionalInfo = parameters.get("additionalInfo", "");

        try
        {
            errorReportingService.reportError(requestMarker, time, url, stackTrace, additionalInfo);
        }
        catch(Exception e)
        {
            throw new ProcessingException("a problem occured while sending error report", e);
        }
    }
}
