package com.github.professorSam.db.model;

import java.util.UUID;

public final class Player {
    private final UUID id;
    private final String name;
    private final Nationality nationality;
    private final Group group;

    public Player(UUID id, String name, String nationality, Group group) {
        this.id = id;
        this.name = name;
        if(nationality.equalsIgnoreCase("german")){
            this.nationality = Nationality.GERMAN;
        } else {
            this.nationality = Nationality.FRENCH;
        }
        this.group = group;
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Nationality nationality() {
        return nationality;
    }

    public Group group() {
        return group;
    }

    public enum Nationality {
        GERMAN,
        FRENCH
    }

}
