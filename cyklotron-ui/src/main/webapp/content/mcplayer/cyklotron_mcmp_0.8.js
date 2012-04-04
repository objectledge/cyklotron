/*
 *   mc player javascript init-file customized for Cyklotron system
 *   
 */

function McPlayer(playerFile, fpFileURL, playerSize)
{
  this.playerFile = playerFile;
  this.fpFileURL = fpFileURL;
  this.playerSize = playerSize;
  this.setDefParams = setDefParams;
  this.initParams = initParams;
  this.write = write;
  this.initParams();
  this.setDefParams();
}

/* Config Variables - Uncomment and edit values to customize your player.
Note: Config variables are normally defined on the web page for maximum versatility, which is why they are commented out in this file.
Any variables defined below will override web page variables and affect all instances of the player that refer to this file.
If you define the variables on the web page instead, you can configure each player differently. */

function setDefParams()
{
//this.streamingServerURL = "";
//this.fpPreviewImageURL = "";
//this.fpAction = "";
//this.colorScheme = "";
//this.cpBackgroundColor = "000000";
//this.cpBackgroundOpacity = "60";
//this.cpButtonsOpacity = "100";
//this.cpCounterPosition = "330x4";
//this.cpFullscreenBtnPosition = "454x12";
//this.cpHidePanel = "button";
//this.cpHideDelay = "0";
//this.cpInfoBtnPosition = "470x12";
//this.cpPlayBtnPosition = "60x12";
//this.cpPlayBtnColor = "";
//this.cpPosition = "0x246";
//this.cpRepeatBtnPosition = "438x12";
//this.cpScrubberPosition = "194x8";
//this.cpScrubberColor = "";
//this.cpScrubberLoadedColor = "";
//this.cpScrubberElapsedColor = "";
//this.cpVolumeStart = "100";
//this.cpStopBtnPosition = "85x12";
//this.cpStopBtnColor = "";
//this.cpVolumeBtnPosition = "118x2";
//this.cpVolumeCtrlColor = "";
//this.cpSize = "480x24";
//this.defaultBufferLength = "1";
//this.defaultEndAction = "previewImage";
//this.defaultStopAction = "previewImage";
//this.fpButtonOpacity = "60";
//this.fpButtonPosition = "240x118";
//this.fpButtonSize = "126x126";
//this.fpButtonColor = "";
//this.fpPreviewImageSize = "fit";
//this.msgBackgroundColor = "000000";
//this.msgBackgroundOpacity = "90";
//this.playerBackgroundColor = "525252";
//this.playerAutoResize = "on";
//this.videoScreenPosition = "0x0";
//this.videoScreenSize = "480x270";
//this.tooltipTextColor = "000000";
//this.tooltipBGColor = "CCCCCC";
}

function initParams()
{
this.streamingServerURL = "";
this.fpPreviewImageURL = "";
this.fpAction = "";
this.colorScheme = "";
this.cpBackgroundColor = "";
this.cpBackgroundOpacity = "";
this.cpButtonsOpacity = "";
this.cpCounterPosition = "";
this.cpFullscreenBtnPosition = "";
this.cpHidePanel = "";
this.cpHideDelay = "";
this.cpInfoBtnPosition = "";
this.cpPlayBtnPosition = "";
this.cpPlayBtnColor = "";
this.cpPosition = "";
this.cpRepeatBtnPosition = "";
this.cpScrubberPosition = "";
this.cpScrubberColor = "";
this.cpScrubberLoadedColor = "";
this.cpScrubberElapsedColor = "";
this.cpVolumeStart = "";
this.cpStopBtnPosition = "";
this.cpStopBtnColor = "";
this.cpVolumeBtnPosition = "";
this.cpVolumeCtrlColor = "";
this.cpSize = "";
this.defaultBufferLength = "";
this.defaultEndAction = "";
this.defaultStopAction = "";
this.fpButtonOpacity = "";
this.fpButtonPosition = "";
this.fpButtonSize = "";
this.fpButtonColor = "";
this.fpPreviewImageSize = "";
this.msgBackgroundColor = "";
this.msgBackgroundOpacity = "";
this.playerBackgroundColor = "";
this.playerAutoResize = "";
this.videoScreenPosition = "";
this.videoScreenSize = "";
this.tooltipTextColor = "";
this.tooltipBGColor = "";
}



function write(divname)
{
if (typeof this.playerFile == 'undefined') { this.playerFile = 'mcmp.swf'; }
if (typeof this.fpFileURL != 'undefined') { this.mcflashvars = 'fpFileURL='+this.fpFileURL; }
if (typeof this.playerSize == 'undefined') { this.playerSize = '480x270'; }

var psep = this.playerSize.indexOf("x");
this.playerWidth = this.playerSize.substring(0,psep);
this.playerHeight = this.playerSize.substring(psep+1);

if (typeof this.streamingServerURL != 'undefined') { this.mcflashvars += '&streamingServerURL='+this.streamingServerURL; }
if (typeof this.fpAction != 'undefined') { this.mcflashvars += '&fpAction='+this.fpAction; }
if (typeof this.fpPreviewImageURL != 'undefined') { this.mcflashvars += '&fpPreviewImageURL='+this.fpPreviewImageURL; }
if (typeof this.colorScheme != 'undefined') { this.mcflashvars += '&colorScheme='+this.colorScheme; }
if (typeof this.cpBackgroundColor != 'undefined') { this.mcflashvars += '&cpBackgroundColor='+this.cpBackgroundColor; }
if (typeof this.cpBackgroundOpacity != 'undefined') { this.mcflashvars += '&cpBackgroundOpacity='+this.cpBackgroundOpacity; }
if (typeof this.cpButtonsOpacity != 'undefined') { this.mcflashvars += '&cpButtonsOpacity='+this.cpButtonsOpacity; }
if (typeof this.cpCounterPosition != 'undefined') { this.mcflashvars += '&cpCounterPosition='+this.cpCounterPosition; }
if (typeof this.cpFullscreenBtnPosition != 'undefined') { this.mcflashvars += '&cpFullscreenBtnPosition='+this.cpFullscreenBtnPosition; }
if (typeof this.cpHideDelay != 'undefined') { this.mcflashvars += '&cpHideDelay='+this.cpHideDelay; }
if (typeof this.cpHidePanel != 'undefined') { this.mcflashvars += '&cpHidePanel='+this.cpHidePanel; }
if (typeof this.cpInfoBtnPosition != 'undefined') { this.mcflashvars += '&cpInfoBtnPosition='+this.cpInfoBtnPosition; }
if (typeof this.cpPlayBtnPosition != 'undefined') { this.mcflashvars += '&cpPlayBtnPosition='+this.cpPlayBtnPosition; }
if (typeof this.cpPlayBtnColor != 'undefined') { this.mcflashvars += '&cpPlayBtnColor='+this.cpPlayBtnColor; }// New in v0.8
if (typeof this.cpPosition != 'undefined') { this.mcflashvars += '&cpPosition='+this.cpPosition; }
if (typeof this.cpRepeatBtnPosition != 'undefined') { this.mcflashvars += '&cpRepeatBtnPosition='+this.cpRepeatBtnPosition; }
if (typeof this.cpScrubberPosition != 'undefined') { this.mcflashvars += '&cpScrubberPosition='+this.cpScrubberPosition; }
if (typeof this.cpScrubberColor != 'undefined') { this.mcflashvars += '&cpScrubberColor='+this.cpScrubberColor; }// New in v0.8
if (typeof this.cpScrubberLoadedColor != 'undefined') { this.mcflashvars += '&cpScrubberLoadedColor='+this.cpScrubberLoadedColor; }// New in v0.8
if (typeof this.cpScrubberElapsedColor != 'undefined') { this.mcflashvars += '&cpScrubberElapsedColor='+this.cpScrubberElapsedColor; }// New in v0.8
if (typeof this.cpVolumeStart != 'undefined') { this.mcflashvars += '&cpVolumeStart='+this.cpVolumeStart; }
if (typeof this.cpStopBtnPosition != 'undefined') { this.mcflashvars += '&cpStopBtnPosition='+this.cpStopBtnPosition; }
if (typeof this.cpStopBtnColor != 'undefined') { this.mcflashvars += '&cpStopBtnColor='+this.cpStopBtnColor; }// New in v0.8
if (typeof this.cpVolumeBtnPosition != 'undefined') { this.mcflashvars += '&cpVolumeBtnPosition='+this.cpVolumeBtnPosition; }
if (typeof this.cpVolumeCtrlColor != 'undefined') { this.mcflashvars += '&cpVolumeCtrlColor='+this.cpVolumeCtrlColor; }// New in v0.8
if (typeof this.cpSize != 'undefined') { this.mcflashvars += '&cpSize='+this.cpSize; }
if (typeof this.defaultBufferLength != 'undefined') { this.mcflashvars += '&defaultBufferLength='+this.defaultBufferLength; }
if (typeof this.defaultEndAction != 'undefined') { this.mcflashvars += '&defaultEndAction='+this.defaultEndAction; }
if (typeof this.defaultStopAction != 'undefined') { this.mcflashvars += '&defaultStopAction='+this.defaultStopAction; }
if (typeof this.fpButtonOpacity != 'undefined') { this.mcflashvars += '&fpButtonOpacity='+this.fpButtonOpacity; }
if (typeof this.fpButtonPosition != 'undefined') { this.mcflashvars += '&fpButtonPosition='+this.fpButtonPosition; }
if (typeof this.fpButtonSize != 'undefined') { this.mcflashvars += '&fpButtonSize='+this.fpButtonSize; }
if (typeof this.fpButtonColor != 'undefined') { this.mcflashvars += '&fpButtonColor='+this.fpButtonColor; }// New in v0.8
if (typeof this.fpPreviewImageSize != 'undefined') { this.mcflashvars += '&fpPreviewImageSize='+this.fpPreviewImageSize; }
if (typeof this.msgBackgroundColor != 'undefined') { this.mcflashvars += '&msgBackgroundColor='+this.msgBackgroundColor; }
if (typeof this.msgBackgroundOpacity != 'undefined') { this.mcflashvars += '&msgBackgroundOpacity='+this.msgBackgroundOpacity; }
if (typeof this.playerBackgroundColor != 'undefined') { this.mcflashvars += '&playerBackgroundColor='+this.playerBackgroundColor; }
if (typeof this.playerAutoResize != 'undefined') { this.mcflashvars += '&playerAutoResize='+this.playerAutoResize; }// New in 0.7
if (typeof this.playerSize != 'undefined') { this.mcflashvars += '&playerSize='+this.playerSize; }
if (typeof this.videoScreenSize != 'undefined') { this.mcflashvars += '&videoScreenSize='+this.videoScreenSize; }
if (typeof this.videoScreenPosition != 'undefined') { this.mcflashvars += '&videoScreenPosition='+this.videoScreenPosition; }
if (typeof this.tooltipTextColor != 'undefined') { this.mcflashvars += '&tooltipTextColor='+this.tooltipTextColor; }// New in 0.7
if (typeof this.tooltipBGColor != 'undefined') { this.mcflashvars += '&tooltipBGColor='+this.tooltipBGColor; }// New in 0.7

	this.str='';
	this.str+='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http:\/\/download.macromedia.com\/pub\/shockwave\/cabs\/flash\/swflash.cab#version=7,0,19,0" width="'+this.playerWidth+'" height="'+this.playerHeight+'">\n';
	this.str+='<param name="movie" value="'+this.playerFile+'">';
	this.str+='<param name="allowScriptAccess" value="always">';
	this.str+='<param name="quality" value="high">';
	this.str+='<param name="allowFullScreen" value="true">';
	this.str+='<param name="FlashVars" value="'+this.mcflashvars+'">\n';
	this.str+='<embed src="'+this.playerFile+'" width="'+this.playerWidth+'" height="'+this.playerHeight+'" quality="high" allowFullScreen="true" allowscriptaccess="always" pluginspage="http:\/\/www.macromedia.com\/go\/getflashplayer" type="application\/x-shockwave-flash" FlashVars="'+this.mcflashvars+'"><\/embed>\n';
	this.str+='<\/object>';
	if(document.getElementById(divname))
	{
	    document.getElementById(divname).innerHTML = this.str;
	}else{
	    document.write(this.str);
	}
}