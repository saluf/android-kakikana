package com.salab.project.kakikana.model;

import com.salab.project.kakikana.model.jisho.JishoJapanese;
import com.salab.project.kakikana.model.jisho.JishoSense;

public class CommonUse {

    private String kanji;
    private String kana;
    private String meaning;

    public CommonUse() {
    }

    public CommonUse(JishoJapanese japanese, JishoSense sense) {
        if (japanese != null){
            this.kanji = japanese.getWord();
            this.kana = japanese.getReading();
        }
        if (sense != null && sense.getDefinition().size() > 0){

            StringBuilder singleDefString = new StringBuilder();
            for (String def : sense.getDefinition()){
                singleDefString.append(def);
                singleDefString.append(";");
            }
            this.meaning = singleDefString.toString();
        }
    }


    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public String getKana() {
        return kana;
    }

    public void setKana(String kana) {
        this.kana = kana;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
