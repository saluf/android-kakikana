package com.salab.project.kakikana.model.jisho;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JishoData {

    @SerializedName("is_common")
    private boolean isCommon;
    private List<JishoJapanese> japanese;
    private List<JishoSense> senses;

    public boolean isCommon() {
        return isCommon;
    }

    public void setCommon(boolean common) {
        isCommon = common;
    }

    public List<JishoJapanese> getJapanese() {
        return japanese;
    }

    public void setJapanese(List<JishoJapanese> japanese) {
        this.japanese = japanese;
    }

    public List<JishoSense> getSenses() {
        return senses;
    }

    public void setSenses(List<JishoSense> senses) {
        this.senses = senses;
    }
}
