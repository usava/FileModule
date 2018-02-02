package com.example.cowboy.filemodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class SaveFilesActivity extends AppCompatActivity implements View.OnClickListener {

    IFileControllerImpl fileController = new IFileControllerImpl();
    PersonModel personModel = new PersonModel();
    FileModel fileModel = new FileModel();
    ArrayList<PersonModel> personList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_files);

        Button btn_saveKml = (Button) findViewById(R.id.btn_savefiles_savekml);
        Button btn_saveXml = (Button) findViewById(R.id.btn_savefiles_savexml);
        Button btn_saveCsv = (Button) findViewById(R.id.btn_savefiles_savecsv);

        btn_saveKml.setOnClickListener(this);
        btn_saveXml.setOnClickListener(this);
        btn_saveCsv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v == findViewById(R.id.btn_savefiles_savekml)){
            this.fileController.writeKMLFile("SlavaPoliakov", getPersonList());
        }else if(v == findViewById(R.id.btn_savefiles_savexml)){
            this.fileController.writeXMLFile("SlavaPoliakov", getFileModel());
        }else if(v == findViewById(R.id.btn_savefiles_savecsv)){
            this.fileController.writeCSVFile("SlavaPoliakov", getPersonList());
        }
    }

    public ArrayList<PersonModel> getPersonList(){
        for(int i=1; i<10; i++){
            Log.d("Slava", " i = "+i);
            PersonModel pm = personModel.mockModel(i);
            Log.d("Slava", " pM = "+pm.getName());
            personList.add(pm);
        }

        return personList;
    }

    public FileModel getFileModel(){
        for(int i=1; i<10; i++){
            Log.d("Slava", " i = "+i);
            fileModel.mockModel(i);
        }

        return fileModel;
    }
}
