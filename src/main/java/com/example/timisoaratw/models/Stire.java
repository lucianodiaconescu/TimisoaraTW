package com.example.timisoaratw.models;

public class Stire {
    private String nume;
    private String descriere;
    private String data;

    public Stire(String nume, String descriere, String data) {
        this.nume = nume;
        this.descriere = descriere;
        this.data = data;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
