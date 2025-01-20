package com.marzane.notes_app.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.marzane.notes_app.ActionValues;
import com.marzane.notes_app.R;
import com.marzane.notes_app.customDialogs.CustomDialogInformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileUtil {

    private static Resources resources;
    private static CustomDialogInformation cdd;

    private FileUtil(){}

    /**
     *
     * @param uri
     * @param activity
     * @return String | null
     */
    public static String readFile(Uri uri, Activity activity){
        String textoLeer = "";

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(activity.getContentResolver().openInputStream(uri)));
            String linea;

            if((linea = br.readLine()) != null) textoLeer += linea;

            while ((linea = br.readLine()) != null){
                textoLeer += "\n" + linea;
            }

            br.close();

        }
        catch (Exception ex)
        {
            cdd = new CustomDialogInformation(activity, ex.getLocalizedMessage(), ActionValues.CLOSE_ACTIVITY.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return null;
        }

        return textoLeer;
    }


    /**
     *
     * @param uri
     * @param text
     * @param activity
     * @return boolean
     */
    public static boolean writeFile(@NonNull Uri uri, @NonNull String text, Activity activity) {

        try {
            OutputStream outputStream = activity.getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();

            return true;

        } catch (IOException ex) {
            cdd = new CustomDialogInformation(activity, ex.getLocalizedMessage(), ActionValues.NOACTION.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return false;
        }

    }


    /**
     *
     * @param realUri
     * @param text
     * @param activity
     * @return boolean
     */
    public static boolean overwriteFile(@NonNull String realUri, @NonNull String text, Activity activity) {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(realUri));
            bw.write(text);
            bw.flush();
            bw.close();

            return true;

        } catch (IOException ex) {
            cdd = new CustomDialogInformation(activity, ex.getLocalizedMessage(), ActionValues.NOACTION.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return false;
        }

    }


    /**
     *
     * @param activity
     * @param realPath "dir1/dir2/file.txt"
     * @param newName "newName.txt"
     * @return boolean
     */
    public static boolean renameFile(Activity activity, String realPath, String newName) {
        Boolean result = false;

        try{
            //String currentFileName = realPath.substring(realPath.lastIndexOf("/") + 1); // original file name

            File directory = new File(realPath.substring(0, realPath.lastIndexOf("/")));   // directory

            // if directory exists
            File from      = new File(realPath);                // original file
            File to        = new File(directory, newName);  // future renamed file

            // if File from exists
            result = from.renameTo(to);

        } catch (Exception ex){
            cdd = new CustomDialogInformation(activity, ex.getLocalizedMessage(), ActionValues.NOACTION.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        }

        return result;
    }


    /**
     * @param activity
     */
    public static void openFileIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // cargar el selector de archivos
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(activity.getResources().getString(R.string.mime_type));
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // opcional, especifica la ubicacion que deberia abrirse al crear el archivo
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.startActivityForResult(intent, ActionValues.OPEN_FILE_PROVIDER.getID());
    }
}
