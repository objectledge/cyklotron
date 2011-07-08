CKEDITOR.editorConfig = function( config )
{
	
	config.toolbarCanCollapse = false;
	
	config.toolbar_Full = [ ['Format'], 
	                   ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
                   	   ['Cut','Copy','Paste','PasteText','PasteFromWord','-','SpellChecker'],
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
	                              ['Cut','Copy','Paste','PasteText','PasteFromWord','-','SpellChecker'],
	                              ['Undo','Redo','-','Find','Replace'],
	                              ['NumberedList','BulletedList'],
	                              ['Link','Unlink']
								] ;

	config.toolbar_Basic = [ ['Bold','Italic','-','NumberedList','BulletedList','-','Link','Unlink','-','About'] ] ;

	
	config.keystrokes = [
	                    	[ CKEDITOR.CTRL + 65 /*A*/, true ],
	                    	[ CKEDITOR.CTRL + 67 /*C*/, true ],
	                    	[ CKEDITOR.CTRL + 70 /*F*/, true ],
	                    	[ CKEDITOR.CTRL + 83 /*S*/, true ],
	                    	[ CKEDITOR.CTRL + 84 /*T*/, true ],
	                    	[ CKEDITOR.CTRL + 88 /*X*/, true ],
	                    	[ CKEDITOR.CTRL + 86 /*V*/, 'Paste' ],
	                    	[ CKEDITOR.CTRL + 45 /*INS*/, true ],
	                    	[ CKEDITOR.SHIFT + 45 /*INS*/, 'Paste' ],
	                    	[ CKEDITOR.CTRL + 88 /*X*/, 'Cut' ],
	                    	[ CKEDITOR.SHIFT + 46 /*DEL*/, 'Cut' ],
	                    	[ CKEDITOR.CTRL + 90 /*Z*/, 'Undo' ],
	                    	[ CKEDITOR.CTRL + 89 /*Y*/, 'Redo' ],
	                    	[ CKEDITOR.SHIFT + 90 /*Z*/, 'Redo' ],
	                    	[ CKEDITOR.CTRL + 76 /*L*/, 'Link' ],
	                    	[ CKEDITOR.CTRL + 66 /*B*/, 'Bold' ],
	                    	[ CKEDITOR.CTRL + 73 /*I*/, 'Italic' ],
	                    	[ CKEDITOR.CTRL + 85 /*U*/, 'Underline' ],
	                    	[ CKEDITOR.SHIFT + 83 /*S*/, 'Save' ],
	                    	[ CKEDITOR.ALT + 13 /*ENTER*/, 'Maximize' ]
	                    ] ;
	
	config.undoStackSize = 30;
	
	config.removeFormatTags = 'b,big,code,del,dfn,em,font,i,ins,kbd,q,samp,small,span,strike,strong,sub,sup,tt,u,var,hr,meta,link,xml';	
	
	config.extraPlugins = 'cyklopagebreak,cykloaddextresource';
}

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