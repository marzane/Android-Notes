package com.marzane.notes_app.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.marzane.notes_app.ActionValues;
import com.marzane.notes_app.R;
import com.marzane.notes_app.customDialogs.CustomDialogInformation;
import com.marzane.notes_app.models.NoteModel;

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

    public static String readFile(Uri uri, Activity activity){
        String textoLeer = "";

        try
        {
            // abro y leo el contenido del archivo, luego lo pego en el editText del editor
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


    public static boolean writeFile(@NonNull Uri uri, @NonNull String texto, Activity activity) {

        try {
            OutputStream outputStream = activity.getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(texto);
            bw.flush();
            bw.close();

            Toast.makeText(activity, activity.getResources().getString(R.string.file_saved), Toast.LENGTH_SHORT).show();
            return true;

        } catch (IOException ex) {
            //Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
            cdd = new CustomDialogInformation(activity, ex.getLocalizedMessage(), ActionValues.NOACTION.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return false;
        }

    }


    public static boolean overwriteFile(@NonNull String realUri, @NonNull String texto, Activity activity) {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(realUri));
            bw.write(texto);
            bw.flush();
            bw.close();

            Toast.makeText(activity, activity.getResources().getString(R.string.file_saved), Toast.LENGTH_SHORT).show();
            return true;

        } catch (IOException ex) {
            cdd = new CustomDialogInformation(activity, ex.getLocalizedMessage(), ActionValues.NOACTION.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return false;
        }

    }


    private boolean renameFile(String path, String newName) {
        Boolean result = false;
        String currentFileName = path.substring(path.lastIndexOf("/") + 1); // original file name
        //currentFileName = currentFileName.substring(1);
        //Log.i("Current file name", currentFileName);

        File directory = new File(path.substring(0, path.lastIndexOf("/")));   // directory

        if(directory.exists()){
            File from      = new File(path);                // original file
            File to        = new File(directory, newName);  // future renamed file
            if (from.exists()) {
                result = from.renameTo(to);
            }

        }
        return result;
    }


    // open file button listener
    public static void openFileIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // cargar el selector de archivos
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(activity.getResources().getString(R.string.mime_type));
        //intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // opcional, especifica la ubicacion que deberia abrirse al crear el archivo
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.startActivityForResult(intent, ActionValues.OPEN_FILE_PROVIDER.getID());
    }
}
