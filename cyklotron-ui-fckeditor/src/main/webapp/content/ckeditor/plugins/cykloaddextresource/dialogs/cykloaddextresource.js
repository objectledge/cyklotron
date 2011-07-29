CKEDITOR.dialog.add( 'cykloaddextresource', function( editor ) {
	   return {
	      title : editor.lang.cykloaddextresource.dialog_title, minWidth : 500, minHeight : 100,
	      contents : [ { id : 'cykloaddextresource_dialog', label : '', title : '', expand : true, padding : 10, elements : [ { id : 'cykloaddextresource_url', type : 'text', label : editor.lang.cykloaddextresource.dialog_input_label, validate : CKEDITOR.dialog.validate.regex(/^(?:http|https):\/\/.*$/,editor.lang.cykloaddextresource.dialog_input_validation) } ] } ],
              onOk: insertResourceOntoEditor,
              onCancle: function(){},
              buttons : [ CKEDITOR.dialog.okButton, CKEDITOR.dialog.cancelButton],
              resizable: CKEDITOR.DIALOG_RESIZE_NONE
	   };
} );

function insertResourceOntoEditor()
{
       elem = this.getContentElement('cykloaddextresource_dialog','cykloaddextresource_url');
       var media_source_url = elem.getValue();
       // Check if youtube source url
       if(media_source_url.indexOf('youtube.com/watch?v=') != -1)
       {
           media_source_url = media_source_url.replace('/watch?v=','/embed/'); 
       }else
    	 // Check if vimeo source url
         if(media_source_url.indexOf('vimeo.com/') != -1)
         {
           media_source_url = media_source_url.replace('vimeo.com/','player.vimeo.com/video/');
         }else{
           // Do nothing. Paste string as defined in inputbox.
         }
       }
       this._.editor.insertHtml('<P><span id="embeddedMedia"><iframe src="'+media_source_url+'">&nbsp;</iframe></span></P>');
}



