package com.marzane.notepad.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;

import com.marzane.notepad.ActionValues;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileUtil {

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

            return false;
        }

    }


    /**
     *
     * @param realUri
     * @param text
     * @return boolean
     */
    public static boolean overwriteFile(@NonNull String realUri, @NonNull String text) {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(realUri));
            bw.write(text);
            bw.flush();
            bw.close();

            return true;

        } catch (IOException ex) {

            return false;
        }

    }


    public static boolean overwriteFileStream(Context context, Uri uri, String text) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        OutputStream outputStream = contentResolver.openOutputStream(uri, "wt");

        if (outputStream == null) {
            throw new IOException();
        }

        try {
            outputStream.write(text.getBytes());
            return true;

        } finally {
            outputStream.close();
        }
    }



    /**
     *
     * @param realPath "dir1/dir2/file.txt"
     * @param newName "newName.txt"
     * @return boolean
     */
    public static boolean renameFile(String realPath, String newName) {
        try{
            File directory = new File(realPath.substring(0, realPath.lastIndexOf("/")));   // directory

            // if directory exists
            File from      = new File(realPath);                // original file
            File to        = new File(directory, newName);  // future renamed file

            // if File from exists
            return from.renameTo(to);

        } catch (Exception ex){
            return false;
        }

    }


    public static boolean deleteFile(String realUri){
        boolean result = false;
        File file = new File(realUri);

        if(file.exists()) {
            result = file.delete();
        }

        return result;
    }


    /**
     * @param activity
     */
    public static void openFileIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // cargar el selector de archivos
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"text/*", "application/javascript"});

        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // opcional, especifica la ubicacion que deberia abrirse al crear el archivo
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        activity.startActivityForResult(intent, ActionValues.OPEN_FILE_PROVIDER.getID());
    }


    public static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }


    public static String copyFileToInternalStorage(Context mContext, Uri uri, String newDirName) {
        Uri returnUri = uri;

        Cursor returnCursor = mContext.getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        }, null, null, null);


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File output;
        if (!newDirName.equals("")) {
            File dir = new File(mContext.getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(mContext.getFilesDir() + "/" + newDirName + "/" + name);
        } else {
            output = new File(mContext.getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }

        return output.getPath();
    }


    public static boolean copyFile(String from, String to) {
        boolean result = false;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(from);
            if (oldfile.exists()) {
                Log.d("ifExists", "Old file exists! ");
                InputStream inStream = new FileInputStream(from);
                FileOutputStream fs = new FileOutputStream(to);
                Log.d("ifExists", "copyFile: " + fs);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
                Log.d("ifExists", "File has been created ");
                result =  true;
            }

        } catch (IOException e1) {
            e1.printStackTrace();
            result = false;
        }

        return result;
    }


}
