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
    if(media_source_url.indexOf('youtube.com/watch?v=') != -1)
    {
        if(media_source_url.indexOf('&') != -1){
          // remove unwanted params
          media_source_url = media_source_url.substring(0, media_source_url.indexOf('&'));
        }
        media_source_url = media_source_url.replace('/watch?v=','/embed/');
        media_source_url += "?rel=0"; // do not show other movies after end.
    }else if(media_source_url.indexOf('vimeo.com/') != -1){
        if(media_source_url.indexOf('?') != -1){
          // remove unwanted params
          media_source_url = media_source_url.substring(0, media_source_url.indexOf('?'));
        }
        media_source_url = media_source_url.replace('vimeo.com/','player.vimeo.com/video/');
    }else{
        // Do nothing. Paste string as defined in inputbox.
    }
    this._.editor.insertHtml('<P><span id="embeddedMedia"><iframe src="'+media_source_url+'">&nbsp;</iframe></span></P>');
}



