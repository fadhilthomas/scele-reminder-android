package com.wibudev.scere.model;

public class Tasks {

    private String link;
    private String name;
    private String matkul;
    private String tgl;

    public Tasks(){

    }

    public Tasks(String link, String name, String matkul, String tgl) {
        this.link = link;
        this.name = name;
        this.matkul = matkul;
        this.tgl = tgl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }
}
