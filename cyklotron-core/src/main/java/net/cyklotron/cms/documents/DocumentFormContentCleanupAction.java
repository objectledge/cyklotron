package net.cyklotron.cms.documents;

import java.io.StringWriter;

import org.dom4j.Document;
import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.Instance;
import org.objectledge.forms.internal.ui.Action;
import org.objectledge.forms.internal.ui.ActionEvent;
import org.objectledge.forms.internal.ui.NodeControl;
import org.objectledge.forms.internal.ui.UI;
import org.objectledge.html.HTMLException;
import org.objectledge.html.HTMLService;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class DocumentFormContentCleanupAction
    extends Action
{
    private final HTMLService htmlService;

    private final String cleanupProfile;

    public DocumentFormContentCleanupAction(HTMLService htmlService, String cleanupProfile)
        throws ConstructionException
    {
        super("cleanupContent", createAtts());
        this.htmlService = htmlService;
        this.cleanupProfile = cleanupProfile;
    }

    @Override
    public void execute(UI ui, Instance instance, ActionEvent evt)
    {
        final NodeControl targetNode = (NodeControl)evt.getTarget();
        String content = (String)targetNode.getValue(instance);
        StringWriter errorWriter = new StringWriter();
        try
        {
            Document dom = htmlService.textToDom4j(content, errorWriter, cleanupProfile);
            StringWriter contentWriter = new StringWriter();
            htmlService.dom4jToText(dom, contentWriter, true);
            targetNode.setValue(instance, contentWriter.toString());
        }
        catch(HTMLException e)
        {
            // uh oh, not much we can do here.
        }
    }

    private static Attributes createAtts()
    {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", "event", "event", "", "cleanupContent");
        return atts;
    }
}
