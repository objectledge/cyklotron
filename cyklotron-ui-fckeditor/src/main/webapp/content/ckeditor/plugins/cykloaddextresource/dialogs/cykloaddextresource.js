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
       //var writer = new CKEDITOR.htmlWriter();
       elem = this.getContentElement('cykloaddextresource_dialog','cykloaddextresource_url');
       //var fragment = CKEDITOR.htmlParser.fragment.fromHtml('<span id="embeddedMedia"><iframe src="'+elem.getValue()+'">&nbsp;</iframe></span>' );
       //fragment.writeHtml(writer);
       //this._.editor.insertHtml(writer.getHtml());
       this._.editor.insertHtml('<P><span id="embeddedMedia"><iframe src="'+elem.getValue()+'">&nbsp;</iframe></span></P>');
}



