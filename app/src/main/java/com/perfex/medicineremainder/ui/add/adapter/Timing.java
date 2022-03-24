package com.perfex.medicineremainder.ui.add.adapter;

public class Timing {
    private String title;
    private boolean selected;
    private String time;
    private String[] radioList;

    public String[] getRadioList() {
        return radioList;
    }

    public void setRadioList(String[] radioList) {
        this.radioList = radioList;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isSelected() {
        return selected;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
