package com.salab.project.kakikana.model.jisho;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JishoSense {

    @SerializedName("english_definitions")
    private List<String> definition;

    public List<String> getDefinition() {
        return definition;
    }

    public void setDefinition(List<String> definition) {
        this.definition = definition;
    }
}
