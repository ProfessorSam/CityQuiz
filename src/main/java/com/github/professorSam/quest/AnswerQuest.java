package com.github.professorSam.quest;

public class AnswerQuest extends Quest {
    private final String rightAnswer;

    public AnswerQuest(String titleDE, String titleFR, String descriptionDE, String descriptionFR, String id, String rightAnswer) {
        super(titleDE, titleFR, descriptionDE, descriptionFR, id);
        this.rightAnswer = rightAnswer;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }
}
