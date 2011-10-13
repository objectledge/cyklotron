String.prototype.trim = function ()
{
    return this.replace(/^\s+|\s+$/g,"");
}

function formatUrlString(urls)
{  
    return "http://" + urls.replace(/http:\/\//ig,"").replace(/,s+/g," ").replace(/\s\s*/g," http://").trim();
}

function formatEmailString(emails)
{  
    return emails.replace(/[,;]/," ").replace(/\s\s*/g,"; ").trim();
}

/*
 * method convert url field to fit FormTool schema format. 
 */
function convertUrlFiled(field)
{
  if(field && field.value.trim() != '') { field.value = formatUrlString(field.value.trim()); }
}

/*
 * method convert email field to fit FormTool schema format. 
 */
function convertEmailFiled(field)
{
  if(field && field.value.trim() != '') { field.value = formatEmailString(field.value.trim()); }
}

/*
 * method convert url propose document organization field to fit FormTool schema format. 
 */
function convertProposeDocumentUrlFileds(maxOrgs)
{
  
  for(var index=1; index<=maxOrgs;index++)
  { 
	convertUrlFiled(document.getElementById('organization_'+ index + '_www'));
	convertEmailFiled(document.getElementById('organization_'+ index + '_email'));
  }
  convertUrlFiled(document.getElementById('source_url'));
  convertEmailFiled(document.getElementById('proposer_email'));
}

/*
 * method gets video source path from youtube, vimeo url sites. 
 */
function getMovieSourcePath(url) {
	if (url.indexOf('youtube.com/watch?v=') != -1) {
		if (url.indexOf('&') != -1) {
			// remove unwanted params
			url = url.substring(0, url.indexOf('&'));
		}
		url = url.replace('/watch?v=', '/embed/');
		url += "?rel=0"; // do not show other movies after end.
	} else if (url.indexOf('vimeo.com/') != -1) {
		if (url.indexOf('?') != -1) {
			// remove unwanted params
			url = url.substring(0, url.indexOf('?'));
		}
		url = url.replace('vimeo.com/', 'player.vimeo.com/video/');
	} else {url = "";}
	return url;
}
