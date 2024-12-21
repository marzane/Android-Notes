package com.marzane.notes_app.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.marzane.notes_app.ActionValues;
import com.marzane.notes_app.customDialogs.CustomDialogInformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
            //Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
            cdd = new CustomDialogInformation(activity, ex.getMessage(), ActionValues.CLOSE_ACTIVITY.getID());
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

            Toast.makeText(activity, "file saved", Toast.LENGTH_SHORT).show();
            return true;

        } catch (IOException ex) {
            //Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
            cdd = new CustomDialogInformation(activity, ex.getMessage(), ActionValues.NOACTION.getID());
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

            Toast.makeText(activity, "file saved", Toast.LENGTH_SHORT).show();
            return true;

        } catch (IOException ex) {
            //Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
            cdd = new CustomDialogInformation(activity, ex.getMessage(), ActionValues.NOACTION.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return false;
        }

    }


    // open file button listener
    public static void openFileIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // cargar el selector de archivos
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // opcional, especifica la ubicacion que deberia abrirse al crear el archivo
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.startActivityForResult(intent, ActionValues.OPEN_FILE_PROVIDER.getID());
    }
}
