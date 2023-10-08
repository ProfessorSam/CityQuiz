package com.github.professorSam.db.model;

import com.github.professorSam.quest.Quest;

import java.time.Instant;

public record Answer(Player player, Instant answerTimestamp, com.github.professorSam.db.model.Answer.AnswerType type,
                     String content, Quest quest) {


    public enum AnswerType {
        CHOICE("choice"),
        ANSWER("answer"),
        IMAGE("img");

        private final String string;

        AnswerType(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

}
