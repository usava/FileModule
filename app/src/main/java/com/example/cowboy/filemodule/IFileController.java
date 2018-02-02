package com.example.cowboy.filemodule;

import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Cowboy on 19.01.2018.
 */

public interface IFileController {
    boolean isExternalStorageReadable();
    boolean createDirectory();
    void writeCSVFile(String fileName, ArrayList<PersonModel> list);
    void writeXMLFile(String fileName, FileModel list);
    void writeKMLFile(String fileName, ArrayList<PersonModel> list);
    ArrayList<File> getContentDirectory();
    void openFile(String fileName, AppCompatActivity activity);
    void shareFile(String fileName, AppCompatActivity activity);
    boolean deleteSingleFile(String fileName);
    void deleteAllFiles();
    String getPath();
}
