package net.cyklotron.cms.modules.actions.files;

import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;

public class UpdatePreferences
    extends BaseUpdatePreferences
{
	public void modifyNodePreferences(RunData data, Parameters conf)
		throws ProcessingException
	{
		String dir = parameters.get("dir","");
		conf.set("dir",dir);		
	}
}
