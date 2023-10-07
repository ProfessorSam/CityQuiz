package com.github.professorSam.handler;

import com.github.professorSam.Main;
import com.github.professorSam.context.QuestOverviewContext;
import com.github.professorSam.db.Database;
import com.github.professorSam.db.model.Player;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class QuestOverviewGetHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger("QuestOverview");

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String userID = context.cookieStore().get("UserID");
        if(userID == null){
            context.redirect("/");
            logger.info("Recived overview without UserID from " + context.ip());
            return;
        }
        Player player = Database.getPlayer(userID);
        if(player == null){
            logger.info("User with id " + userID + " not found!");
            context.cookieStore().set("UserID", "null");
            context.redirect("/");
            return;
        }
        context.render("questoverview.jte", Collections.singletonMap("context", new QuestOverviewContext(player, Main.getInstance().getQuests())));
    }
}
