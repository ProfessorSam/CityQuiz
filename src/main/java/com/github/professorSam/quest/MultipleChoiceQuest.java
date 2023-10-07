package com.github.professorSam.quest;

import java.util.List;

public class MultipleChoiceQuest extends Quest {
    private final List<String> choices;
    private final String rightAnswer;

    public MultipleChoiceQuest(String titleDE, String titleFR, String descriptionDE, String descriptionFR, String id, List<String> choices, String rightAnswer) {
        super(titleDE, titleFR, descriptionDE, descriptionFR, id);
        this.choices = choices;
        this.rightAnswer = rightAnswer;
    }

    public List<String> getChoices() {
        return choices;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }
}