package com.github.professorSam.quest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class QuestFactory {

    private static final Logger logger = LoggerFactory.getLogger("QuestFactory");

    public static List<Quest> createQuestsFromJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Quest> quests = new ArrayList<>();
        try (InputStream inputStream = QuestFactory.class.getResourceAsStream("/quests.json")){
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            for (JsonNode node : jsonNode) {
                String id = node.get("id").asText();
                String type = node.get("type").asText();
                String titleDE = node.get("titleDE").asText();
                String titleFR = node.get("titleFR").asText();
                String descriptionDE = node.get("descriptionDE").asText();
                String descriptionFR = node.get("descriptionFR").asText();
                if ("answer".equals(type)) {
                    String rightAnswer = node.get("rightAnswer").asText();
                    quests.add(new AnswerQuest(titleDE, titleFR, descriptionDE, descriptionFR, id, rightAnswer));
                } else if ("multiple choice".equals(type)) {
                    JsonNode choicesNode = node.get("choices");
                    List<String> choices = new ArrayList<>();
                    for (JsonNode choice : choicesNode) {
                        choices.add(choice.asText());
                    }
                    String rightAnswer = node.get("rightAnswer").asText();
                    quests.add(new MultipleChoiceQuest(titleDE, titleFR, descriptionDE, descriptionFR, id, choices, rightAnswer));
                } else if ("picture".equals(type)) {
                    quests.add(new PictureQuest(titleDE, titleFR, descriptionDE, descriptionFR, id));
                }
                logger.info("Loaded quest " + id + " with type " + type);
            }
        }
        return quests;
    }
}
