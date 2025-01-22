package com.marzane.notes_app.Utils;

import android.content.Context;
import android.text.Spannable;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextTools {

    private MyClipboardManager myClipboardManager = new MyClipboardManager();
    private EditText editText;
    private Context context;


    public TextTools(Context context, EditText editText){
        this.editText = editText;
        this.context = context;
    }


    public void cut(){
        int start = Math.max(editText.getSelectionStart(), 0);
        int end = Math.max(editText.getSelectionEnd(), 0);
        if(start != end){
            String text = editText.getText().toString();
            Boolean r = myClipboardManager.copyToClipboard(context, text.substring(start, end));
            editText.getText().replace(Math.min(start, end), Math.max(start, end), "", 0, 0);
        }
    }


    public boolean copy(){
        boolean r = false;
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if( start != end){
            String text = editText.getText().toString();

            r = myClipboardManager.copyToClipboard(context, text.substring(start, end));
        }

        return r;
    }


    public void paste(){
        String paste = myClipboardManager.readFromClipboard(context);
        if (!paste.isEmpty()){
            int start = Math.max(editText.getSelectionStart(), 0);
            int end = Math.max(editText.getSelectionEnd(), 0);

            editText.getText().replace(Math.min(start, end), Math.max(start, end), paste, 0, paste.length());

        }
    }


    public String select(){
        //editText.setSelection();
        Spannable textSpan = editText.getText();
        final int selection = editText.getSelectionStart();
        final Pattern pattern = Pattern.compile("\\w+");
        final Matcher matcher = pattern.matcher(textSpan);
        int start = 0;
        int end = 0;

        String currentWord = "";
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            if (start <= selection && selection <= end) {
                currentWord = textSpan.subSequence(start, end).toString();
                editText.setSelection(start, end);
                break;
            }
        }

        return currentWord; // This is current word
    }


    public void selectAll(){
        editText.selectAll();
    }
}
