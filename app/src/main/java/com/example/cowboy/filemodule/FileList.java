package com.example.cowboy.filemodule;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Cowboy on 26.01.2018.
 */

@Root
public class FileList {

    @ElementList
    private ArrayList<FileModel> list;
    @Attribute
    private String name;

    public FileList(ArrayList<FileModel> list, String name) {
        this.list = list;
        this.name = name;
    }

    public ArrayList<FileModel> getList() {
        return list;
    }

    public void setList(ArrayList<FileModel> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FileList{" +
                "list=" + list +
                ", name='" + name + '\'' +
                '}';
    }
}