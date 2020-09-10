package com.salab.project.kakikana.model.jisho;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JishoResponse {

    @SerializedName("data")
    private List<JishoData> dataList;

    public List<JishoData> getDataList() {
        return dataList;
    }

    public void setDataList(List<JishoData> dataList) {
        this.dataList = dataList;
    }

}