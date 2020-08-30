package com.salab.project.kakikana.model;

public class Kana {

    private int id;
    private String type;
    private String kana;
    private String romaji;

    public Kana() {
    }

    public Kana(int id, String type, String kana, String romaji) {
        this.id = id;
        this.type = type;
        this.kana = kana;
        this.romaji = romaji;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKana() {
        return kana;
    }

    public void setKana(String kana) {
        this.kana = kana;
    }

    public String getRomaji() {
        return romaji;
    }

    public void setRomaji(String romaji) {
        this.romaji = romaji;
    }
}
