CKEDITOR.editorConfig = function( config )
{
	
	config.toolbarCanCollapse = false;
	
	config.toolbar_Full = [ ['Format'], 
	                   ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
                   	   ['Cut','Copy','Paste','PasteText','PasteFromWord'],
                   	   ['Undo','Redo','-','Find','Replace'],
                   	   '/',
                   	   ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote','-','CreateDiv'],
                   	   ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
                   	   ['Link','Unlink','Anchor','-','CyklotronAddExtRes'],
                   	   ['Image','Table','HorizontalRule','SpecialChar','CyklotronPageBreak'],
                   	   ['Maximize','ShowBlocks'],
                   	   ['SelectAll','RemoveFormat'],
                   	   ['Source'] 
                     ] ;

	config.toolbar_Restricted = [ ['Bold'],
	                              ['Cut','Copy','Paste','PasteText','PasteFromWord'],
	                              ['Undo','Redo','-','Find','Replace'],
	                              ['NumberedList','BulletedList'],
	                              ['Link','Unlink']
								] ;

	config.toolbar_Basic = [ ['Bold','Italic','-','NumberedList','BulletedList','-','Link','Unlink','-','About'] ] ;
	
	config.undoStackSize = 30;
	
	config.removeFormatTags = 'b,big,code,del,dfn,em,font,i,ins,kbd,q,samp,small,span,strike,strong,sub,sup,tt,u,var,hr,meta,link,xml';	
	
	config.removePlugins = 'scayt';
	config.extraPlugins = 'cyklopagebreak,cykloaddextresource';
};

CKEDITOR.on('dialogDefinition', function( ev )
{
			// Take the dialog name and its definition from the event data.
			var dialogName = ev.data.name;
			var dialogDefinition = ev.data.definition;
	 
			// Check if the definition is from the dialog we're
			// interested on (the Link dialog).
			if ( dialogName == 'link' )
			{
				// FCKConfig.LinkDlgHideAdvanced = true
				dialogDefinition.removeContents( 'advanced' );
	 
				// FCKConfig.LinkDlgHideTarget = true
				//dialogDefinition.removeContents( 'target' );
	
	            // Enable this part only if you don't remove the 'target' tab in the previous block.
	 
				// FCKConfig.DefaultLinkTarget = '_blank'
				// Get a reference to the "Target" tab.
				var targetTab = dialogDefinition.getContents( 'target' );
				// Set the default value for the URL field.
				var targetField = targetTab.get( 'linkTargetType' );
				targetField[ 'default' ] = '_blank';
			}
	 
			if ( dialogName == 'image' )
			{
				// FCKConfig.ImageDlgHideAdvanced = true	
				//dialogDefinition.removeContents( 'advanced' );
				
				// FCKConfig.ImageDlgHideLink = true
				dialogDefinition.removeContents( 'Link' );
			}
	 
			if ( dialogName == 'flash' )
			{
				// FCKConfig.FlashDlgHideAdvanced = true
				//dialogDefinition.removeContents( 'advanced' );
			}
	 
});