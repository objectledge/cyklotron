CKEDITOR.plugins.add('cyklopagebreak',
{
  lang : ['en','pl'], 
  init:function(editor) { 
	var pagebreakCommand = editor.addCommand('pagebreakAction', { exec:function(editor){ editor.insertHtml('<HR class="page-break" />'); }} );
	pagebreakCommand.modes={wysiwyg:1, source:0};
	editor.ui.addButton("CyklotronPageBreak", { label: editor.lang.cyklopagebreak.title, command: 'pagebreakAction', icon:this.path+"pagebreak.gif" });
  }
});
