String.prototype.trim = function ()
{
    return this.replace(/^\s+|\s+$/g,"");
}

function formatUrlString(urls)
{  
    return "http://" + urls.replace(/http:\/\//ig,"").replace(/[^a-zA-Z0-9./:&?=%]/g," ").replace(/\s\s*/g," http://").trim();
}

/*
 * method convert url field to fit FormTool schema format. 
 */
function convertUrlFiled(field)
{
  if(field && field.value.trim() != '') { field.value = formatUrlString(field.value.trim()); }
}

/*
 * method convert url propose document organization field to fit FormTool schema format. 
 */
function convertProposeDocumentUrlFileds(maxOrgs)
{
  
  for(var index=1; index<=maxOrgs;index++)
  { 
	convertUrlFiled(document.getElementById('organization_'+ index + '_www'));
  }
  convertUrlFiled(document.getElementById('source_url'));
}