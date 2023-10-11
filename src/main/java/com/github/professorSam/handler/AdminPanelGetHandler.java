package com.github.professorSam.handler;

import com.github.professorSam.Main;
import com.github.professorSam.context.AdminPanelContext;
import com.github.professorSam.context.ErrorContext;
import com.github.professorSam.db.Database;
import com.github.professorSam.db.model.Answer;
import com.github.professorSam.db.model.Group;
import com.github.professorSam.db.model.Player;
import com.github.professorSam.quest.Quest;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AdminPanelGetHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger("AdminPanel");

    @Override
    public void handle(@NotNull Context context) {
        if(Main.getInstance().getAdminToken() != null){
            String tokenParam = context.queryParam("token");
            if(tokenParam == null){
                context.render("error.jte", Collections.singletonMap("context", new ErrorContext("Keine Berechtigung")));
                return;
            }
            if(!tokenParam.equals(Main.getInstance().getAdminToken())){
                context.render("error.jte", Collections.singletonMap("context", new ErrorContext("Keinen Zugang (unzulässiger admin token)")));
                return;
            }
        }
        long start = Instant.now().toEpochMilli();
        int questCount = Main.getInstance().getQuests().size();
        int groupCount = Database.getGroupCount();
        int playerCount = Database.getPlayerCount();
        int groupsDone = Database.getDoneGroups();
        HashMap<Quest, List<Answer>> answers = Database.getAnswers();
        HashMap<Group, List<Player>> groups = Database.getAllPlayersInGroups();
        AdminPanelContext panelContext = new AdminPanelContext(playerCount,groupCount, questCount, groupsDone, answers, groups);
        context.render("admin.jte", Collections.singletonMap("context", panelContext));
        long end = Instant.now().toEpochMilli();
        logger.info("Rendered in " + (end - start) + "ms");
    }
}
