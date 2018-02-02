package com.example.cowboy.filemodule;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Cowboy on 19.01.2018.
 */

public class IFileControllerImpl implements IFileController {
    @Override
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean createDirectory() {
        File docsFolder;
        if (Build.VERSION.SDK_INT >= 19) {
            docsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!docsFolder.exists()) {
                docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
            }
        } else {
            docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        }

        File myDir = new File(docsFolder, "/FileModel");
        myDir.mkdir();

        if(docsFolder.exists())
            return true;

        return false;
    }

    @Override
    public void writeCSVFile(String fileName, ArrayList<PersonModel> list) {
        FileWriter fileWriter = null;

        File file = new File(getPath(), fileName + ".csv");
        try {
            fileWriter = new FileWriter(file);
            fileWriter.append("name, date, latitude, longitude");//address, distance by start (m)
            fileWriter.append("\n");
            for (PersonModel fileModel : list) {
                fileWriter.append(fileModel.getName());
                fileWriter.append(",");
                fileWriter.append(fileModel.getTime());
                fileWriter.append(",");
                fileWriter.append(String.valueOf(fileModel.getLatitude()));
                fileWriter.append(",");
                fileWriter.append(String.valueOf(fileModel.getLongitude()));
                fileWriter.append(",");
                fileWriter.append("\n");//,
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeXMLFile(String fileName, FileModel fileModel) {
        Serializer serializer = new Persister();

        File file = new File(getPath(), fileName + ".xml");
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\"> " +
                "<Placemark>\n";
        String footer =
                "</Placemark> " +
                        "</kml>";
        String content = ""+fileModel.toString();
        Log.d("Slava", ""+content);
        try {
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(header + content + footer);
            bw.flush();
            bw.close();
            serializer.write(fileModel, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeKMLFile(String fileName, ArrayList<PersonModel> list) {
        File file = new File(getPath(), fileName + ".kml");
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\"> " +
                "<Placemark>\n";
        String footer =
                "</Placemark> " +
                        "</kml>";
        String content = "";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (PersonModel fileModel : list) {
                //if (fileModel.getLatitude() != 0.0d && fileModel.getLongitude() != 0.0d) {
                    content = content + "<name>" + fileModel.getName() + "</name>\n" +
                            "<description>" + " description " + "</description>\n" +
                            "<Point>\n" +
                            "<coordinates>" + String.valueOf(fileModel.getLongitude()) + "," + String.valueOf(fileModel.getLatitude()) + ",0" + "</coordinates>\n" +
                            "</Point>\n";
                //}

            }
            bw.write(header + content + footer);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openFile(String fileName, AppCompatActivity activity) {
        File file = new File(getPath(), fileName);
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);

        Intent inten = new Intent(Intent.ACTION_VIEW);
        inten.setDataAndType(uri, "text/plain");
        inten.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//
        activity.startActivity(Intent.createChooser(inten, ""));
    }

    @Override
    public void shareFile(String fileName, AppCompatActivity activity) {
        File file = new File(getPath(), fileName);
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);

        ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder
                .from(activity)
                .addStream(uri)
                .setType("text/plain")
                .addStream(uri);

        Intent chooserIntent = intentBuilder.createChooserIntent();
        List<ResolveInfo> resolvedIntentActivities = activity.getPackageManager().queryIntentActivities(chooserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(chooserIntent);
        }
    }

    @Override
    public boolean deleteSingleFile(String fileName) {
        File dir = new File(getPath());
        File file = new File(dir, fileName);
        return file.delete();
    }

    @Override
    public void deleteAllFiles() {
        File dir = new File(getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    @Override
    public String getPath() {
        String path;
        if (Build.VERSION.SDK_INT >= 19) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                    + "/FileModel";
        } else {
            path = Environment.getExternalStorageDirectory().toString() + "/Documents";// + "/FileModel";
        }
        return path;
    }


    @Override
    public ArrayList<File> getContentDirectory() {
        File dir = new File(getPath());
        return new ArrayList<>(Arrays.asList(dir.listFiles()));
    }
}
