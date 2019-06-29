package com.alekseyM73.model.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class StructuredFormatting {

    @SerializedName("main_text")
    @Expose
    private String mainText;
    @SerializedName("main_text_matched_substrings")
    @Expose
    private List<MatchedSubstring> mainTextMatchedSubstrings = null;
    @SerializedName("secondary_text")
    @Expose
    private String secondaryText;

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public List<MatchedSubstring> getMainTextMatchedSubstrings() {
        return mainTextMatchedSubstrings;
    }

    public void setMainTextMatchedSubstrings(List<MatchedSubstring> mainTextMatchedSubstrings) {
        this.mainTextMatchedSubstrings = mainTextMatchedSubstrings;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }
}
