package com.github.professorSam.handler;

import com.github.professorSam.Main;
import com.github.professorSam.db.Database;
import com.github.professorSam.db.FileStorage;
import com.github.professorSam.db.model.Answer;
import com.github.professorSam.db.model.Player;
import com.github.professorSam.quest.Quest;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

public class QuestViewPostHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger("QuestUpload");

    @Override
    public void handle(@NotNull Context context) {
        String userID = context.cookieStore().get("UserID");
        if(userID == null || userID.equals("null")){
            context.redirect("/");
            logger.info("unrecognized user for " + context.ip());
            return;
        }
        Player player = Database.getPlayer(userID);
        if(player == null){
            logger.info("Unkown user for id " + userID);
            context.redirect("/");
            return;
        }
        Instant timestamp = Instant.now();
        Quest quest = Main.getInstance().getQuests().get(player.group().quest());
        String answerParam = context.formParam("answer");
        String choice = context.formParam("choice");
        if(answerParam != null){
            logger.info("Answer from " + player.name() + " for " + player.group().name());
            Answer answer = new Answer(player, timestamp, Answer.AnswerType.ANSWER, answerParam, quest);
            Database.addAnswerAndIncrementCurrentQuest(answer);
        }
        else if(choice != null){
            logger.info("Multiple choice answer from " + player.name() + " for " + player.group().name());
            Answer answer = new Answer(player, timestamp, Answer.AnswerType.CHOICE, choice, quest);
            Database.addAnswerAndIncrementCurrentQuest(answer);
        }
        else if(!context.uploadedFiles().isEmpty()){
            logger.info("Picture from " + player.name() + " for " + player.group().name());
            UploadedFile file = context.uploadedFiles().get(0);
            if(file == null){
                logger.info("File not found!");
                context.redirect("/questoverview");
                return;
            }
            if(file.size() < 10){ //Ensure uploaded file is not empty
                logger.info("File too small for " + player.name());
                context.redirect("/questoverview");
                return;
            }
            UUID fileUUID = UUID.randomUUID();
            FileStorage.uploadFile(file.content(), fileUUID.toString());
            Answer answer = new Answer(player, timestamp, Answer.AnswerType.IMAGE, fileUUID.toString(), quest);
            Database.addAnswerAndIncrementCurrentQuest(answer);
        }
        else {
            logger.info("unkwon quest type uploaded for " + player.name() + "! Form params: " + context.formParamMap().keySet());
        }
        context.redirect("/questoverview");
    }
}
