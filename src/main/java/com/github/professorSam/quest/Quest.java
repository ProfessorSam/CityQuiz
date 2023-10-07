package com.github.professorSam.quest;

public class Quest {
    private final String id;
    private final String descriptionFR;
    private final String descriptionDE;
    private final String titleFR;
    private final String titleDE;

    public Quest(String titleDE, String titleFR, String descriptionDE, String descriptionFR, String id) {
        this.descriptionFR = descriptionFR;
        this.descriptionDE = descriptionDE;
        this.titleFR = titleFR;
        this.titleDE = titleDE;
        this.id = id;
    }

    public String getDescriptionFR() {
        return descriptionFR;
    }

    public String getDescriptionDE() {
        return descriptionDE;
    }

    public String getTitleFR() {
        return titleFR;
    }

    public String getTitleDE() {
        return titleDE;
    }

    public String getId(){
        return id;
    }
}
