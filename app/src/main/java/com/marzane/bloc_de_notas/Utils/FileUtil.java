package com.marzane.bloc_de_notas.Utils;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileUtil {

    public static String readFile(Uri uri, Context context){
        String textoLeer = "";

        try
        {
            // abro y leo el contenido del archivo, luego lo pego en el editText del editor
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream(uri)));
            String linea;

            if((linea = br.readLine()) != null) textoLeer += linea;

            while ((linea = br.readLine()) != null){
                textoLeer += "\n" + linea;
            }

            br.close();

        }
        catch (Exception ex)
        {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

        return textoLeer;
    }


    public static boolean writeFile(@NonNull Uri uri, @NonNull String texto, Context context) {

        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(texto);
            bw.flush();
            bw.close();

            Toast.makeText(context, "file saved", Toast.LENGTH_SHORT).show();
            return true;

        } catch (IOException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

    }


    public static boolean overwriteFile(@NonNull String realUri, @NonNull String texto, Context context) {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(realUri));
            bw.write(texto);
            bw.flush();
            bw.close();

            Toast.makeText(context, "file saved", Toast.LENGTH_SHORT).show();
            return true;

        } catch (IOException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

    }

}
