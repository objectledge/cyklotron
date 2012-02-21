CKEDITOR.plugins.add('cykloaddextresource',
{
  lang : ['en','pl'], 
  init:function(editor)
  {
   var pagebreakCommand = editor.addCommand('cykloaddextresource', new CKEDITOR.dialogCommand('cykloaddextresource'));
   CKEDITOR.dialog.add('cykloaddextresource', this.path + 'dialogs/cykloaddextresource.js');
   pagebreakCommand.modes={wysiwyg:1, source:0};
   editor.ui.addButton("CyklotronAddExtRes", { label: editor.lang.cykloaddextresource.button_title, command: 'cykloaddextresource', icon:this.path+"cykloaddextresource.gif" });
  }
});
