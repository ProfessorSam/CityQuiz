package com.github.professorSam.handler;

import com.github.professorSam.db.Database;
import com.github.professorSam.db.model.Player;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        context.redirect("/questoverview");
        if(context.formParam("answer") != null){
            logger.info("Answer from " + player.name() + " for " + player.group().name());
            //TODO handle answer
        }
        if(context.formParam("choice") != null){
            logger.info("Multiple choice answer from " + player.name() + " for " + player.group().name());
            //TODO handle multiple choice
        }
        if(!context.uploadedFiles().isEmpty()){
            logger.info("Picture from " + player.name() + " for " + player.group().name());
            //TODO handle file upload
        }
    }
}
