/*
 * This script was created by Damian Gajda (zwierzem@ngo.pl)
 * Copyright 2003
 *
 * $Id: CMSScriptLoader.js,v 1.1 2005-01-28 02:45:19 pablo Exp $
 */

function CMSScriptLoader(commonBasePath, appBasePath, skinBasePath)
{
    this.commonBasePath = this.fixBasePath(commonBasePath);
    this.appBasePath = this.fixBasePath(appBasePath);
    this.loadedScripts = [];
    this.skinBasePath = this.fixBasePath(skinBasePath);
}

CMSScriptLoader.prototype.loadSkin =
function (relativePath)
{
    this.prototype.load(this.skinBasePath, relativePath);
};

CMSScriptLoader.prototype.fixBasePath =
function (basePath)
{
    if(basePath.charAt(basePath.length-1) == '/')
    {
        basePath = basePath.substr(0, basePath.length-1)
    }
    return basePath;
};

CMSScriptLoader.prototype.loadCommon =
function (relativePath)
{
    this.load(this.commonBasePath, relativePath);
};

CMSScriptLoader.prototype.loadApp =
function (relativePath)
{
    this.load(this.appBasePath, relativePath);
};

CMSScriptLoader.prototype.load =
function (basePath, relativePath)
{
    var path = basePath + '/' + relativePath;

    var alreadyLoaded = false;
    for(var i=0; i<this.loadedScripts.length; i++)
    {
        if(this.loadedScripts[i] == path)
        {
            alreadyLoaded = true;
            break;
        }
    }

    if(!alreadyLoaded)
    {
        this.loadedScripts[this.loadedScripts.length] = path;
        document.write('<script type="text/javascript" language="javascript" src="'
                        + path + '"><\/script>');
    }
};

