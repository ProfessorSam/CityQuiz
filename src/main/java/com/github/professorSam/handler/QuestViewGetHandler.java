package com.github.professorSam.handler;

import com.github.professorSam.Main;
import com.github.professorSam.context.QuestViewContext;
import com.github.professorSam.db.Database;
import com.github.professorSam.db.model.Player;
import com.github.professorSam.quest.Quest;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;

public class QuestViewGetHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger("QuestOverviewGet");

    @Override
    public void handle(@NotNull Context context) {
        String userID = context.cookieStore().get("UserID");
        if(userID == null || userID.equals("null")){
            context.redirect("/");
            return;
        }
        long start = Instant.now().toEpochMilli();
        Player player = Database.getPlayer(userID);
        Quest quest = Main.getInstance().getQuests().get(player.group().quest());
        if(quest == null){
            context.result("Quest not found!");
        }
        context.render("quest.jte", Collections.singletonMap("context", new QuestViewContext(quest, player)));
        long end = Instant.now().toEpochMilli();
        logger.info("in " + (end - start) + "ms for " + player.name() + " in group " + player.group().name());
    }
}
