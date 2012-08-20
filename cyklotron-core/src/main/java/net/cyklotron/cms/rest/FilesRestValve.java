package net.cyklotron.cms.rest;

import javax.servlet.ServletException;

import net.cyklotron.cms.files.FilesService;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.web.rest.JerseyRestValve;

public class FilesRestValve extends JerseyRestValve
{

    protected FilesService filesService;

    public FilesRestValve(Logger logger, Configuration config)
                    throws ConfigurationException, ServletException
    {
        super(logger, config);
        // TODO Auto-generated constructor stub
    }

    public FilesRestValve(Logger logger, Configuration config, FilesService fileService)
                    throws ConfigurationException, ServletException
                {
                    super(logger, config);
                    this.filesService = fileService;
                    // TODO Auto-generated constructor stub
                }

}
