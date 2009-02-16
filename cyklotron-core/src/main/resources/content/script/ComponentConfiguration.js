/**
 * Component configuration helper functions
 *
 * $Id: ComponentConfiguration.js,v 1.1 2005-01-28 02:45:19 pablo Exp $
 */
scriptLoader.loadCommon('Forms.js');

function submitConfig(targetWindowName, sourceFormName, targetFormName, fieldNames)
{
    var srcForm = document.forms[sourceFormName];
    var targetForm = document.forms[targetFormName];

    var config = '';
    for(var i=0; i<fieldNames.length; i++)
    {
        var name = fieldNames[i];
        if(srcForm.elements[name] != null)
        {
            var value = getValue (sourceFormName, name);
            config += name+'='+value+'\n';
        }
        else if(typeof(computeProperty) == 'function')
        {
            var prop = computeProperty(sourceFormName, name);
            if(prop != null)
            {
                config += name+'='+prop+'\n';
            }
        }
    }
    //alert(config);
    targetForm.elements['config'].value = config;
    window.opener.name = targetWindowName;
    targetForm.submit();
    window.close();
}

