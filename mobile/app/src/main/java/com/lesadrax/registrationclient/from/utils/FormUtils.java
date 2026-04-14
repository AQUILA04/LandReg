package com.lesadrax.registrationclient.from.utils;


import static androidx.core.content.ContentProviderCompat.requireContext;

import static com.lesadrax.registrationclient.from.utils.FormIntents.CAMERA_REQUEST;
import static com.lesadrax.registrationclient.from.utils.FormIntents.PICK_FILE_REQUEST;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lesadrax.registrationclient.BuildConfig;
import com.lesadrax.registrationclient.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FormUtils {

    public static String lastSelectedFieldId;
    public static String camFilePath;

    public static String genCameraFilePath(Context context){
        File dir = context.getFilesDir();
        if (dir == null){
            return null;
        }

        String dirPath = dir.getAbsolutePath();
//        String dirPath = context.getFilesDir().getAbsolutePath();

        File dirFile = new File(dirPath);

        if (!dirFile.exists()){
            if (!dirFile.mkdirs())
                return null;
        }

        camFilePath = dirPath+"/CAM_"+System.currentTimeMillis()+".JPEG";;

        return camFilePath;
    }

    public static void openFilePicker(Activity activity, String[] extra, String mime, String name) {

        if (activity != null) {


            if (extraContains(extra,"CAMERA") && extraContains(extra,"FILE")) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(mime);
                Intent chooser = Intent.createChooser(intent, "Select File");

                activity.startActivityForResult(chooser, PICK_FILE_REQUEST);
            } else if (extraContains(extra,"CAMERA")){

                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.CAMERA},
                            100
                    );
                    return;
                }

                String camPath = FormUtils.genCameraFilePath(activity);

                if (camPath == null){
                    Toast.makeText(activity, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                File file = new File(camPath);

                Uri outputFileUri = FileProvider.getUriForFile(activity,
                        BuildConfig.APPLICATION_ID + ".provider", file);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                activity.startActivityForResult(cameraIntent, CAMERA_REQUEST);

            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(mime);
                Intent chooser = Intent.createChooser(intent, "Select File");

                activity.startActivityForResult(chooser, PICK_FILE_REQUEST);
            }

            FormUtils.lastSelectedFieldId = name;
        }
    }

    public static boolean extraContains(String[] extras,String key) {
        if (extras == null) return false;
        for (String extra : extras) {
            if (key.equals(extra)) {
                return true;
            }
        }
        return false;
    }

    public static int getInt(Object data) {
        if (data instanceof Integer) {
            return (Integer) data;
        } else if (data instanceof Long) {
            return ((Long) data).intValue();
        } else if (data instanceof Double) {
            return ((Double) data).intValue();
        } else if (data instanceof String) {
            try {
                return Integer.parseInt((String) data);
            } catch (NumberFormatException e) {
                // If parsing fails, return a default value (e.g., 0)
                return 0;
            }
        }
        // Return a default value if the type is unsupported
        return 0;
    }

    public static Boolean getBool(Object data) {
        if (data instanceof Boolean)
            return (Boolean) data;

        return null;
    }

    public static void FilePickerResult(Context context, int requestCode, int resultCode, Intent data){
        Log.d("UUUUUU", "1");
        if (resultCode == Activity.RESULT_OK) {
            Log.d("UUUUUU", "2");
            if (requestCode == FormIntents.PICK_FILE_REQUEST) {
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    File file = null;
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                        file = streamToFile(context, inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    }

                    if (file == null) {
                        Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String fileName = getFileName(context, uri);

                    // Broadcast the result
                    Intent broadcastIntent = new Intent("FILE_SELECTED");
                    broadcastIntent.putExtra("fieldId", lastSelectedFieldId);
                    broadcastIntent.putExtra("fileName", fileName);
                    broadcastIntent.putExtra("filePath", file.getAbsolutePath());

                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(broadcastIntent);
                    Log.d("UUUUUUU", "3");
                }
            } else if (requestCode == FormIntents.CAMERA_REQUEST) {


                try {

                    File file = new File(camFilePath);
                    if (file.exists()) {

                        Intent broadcastIntent = new Intent("FILE_SELECTED");
                        broadcastIntent.putExtra("fieldId", lastSelectedFieldId);
                        broadcastIntent.putExtra("fileName", file.getName());
                        broadcastIntent.putExtra("filePath", file.getAbsolutePath());


                        LocalBroadcastManager.getInstance(context)
                                .sendBroadcast(broadcastIntent);
                        Log.d("UUUUUUU", "3");
                    } else {
                        Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == FormIntents.TEXT_PICKER_REQUEST) {


                try {

                    File file = new File(camFilePath);
                    if (file.exists()) {

                        Intent broadcastIntent = new Intent("FILE_SELECTED");
                        broadcastIntent.putExtra("fieldId", lastSelectedFieldId);
                        broadcastIntent.putExtra("fileName", file.getName());
                        broadcastIntent.putExtra("filePath", file.getAbsolutePath());


                        LocalBroadcastManager.getInstance(context)
                                .sendBroadcast(broadcastIntent);
                        Log.d("UUUUUUU", "3");
                    } else {
                        Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                }

            }
        }


        lastSelectedFieldId = null;
        camFilePath = null;
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static File streamToFile(Context context, InputStream inputStream){

        File dir = context.getFilesDir();
        if (dir == null){
            return null;
        }

        String dirPath = dir.getAbsolutePath();
//        String dirPath = context.getFilesDir().getAbsolutePath();

        File dirFile = new File(dirPath);

        if (!dirFile.exists()){
            if (!dirFile.mkdirs())
                return null;
        }

        File file = new File(dirPath+"/file_"+System.currentTimeMillis());

        if (file.exists())
            if (!file.delete())
                return null;

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file.getAbsoluteFile());


            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return file;

        } catch (Exception e) {
            e.printStackTrace();
            if (outputStream != null) {
                try {
                    outputStream.close();
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }


        return null;
    }

}
