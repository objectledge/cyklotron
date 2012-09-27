package net.cyklotron.cms.rest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import net.cyklotron.cms.files.FilesService;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.web.rest.JerseyRestValve;

public class FilesRestValve extends JerseyRestValve
{

    protected FilesService filesService;

    public FilesRestValve(Logger logger, final Configuration config,
        final ServletContext servletContext)
                    throws ConfigurationException, ServletException
    {
        super(logger, config, servletContext);
        // TODO Auto-generated constructor stub
    }

}
