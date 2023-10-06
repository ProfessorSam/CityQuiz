package com.github.professorSam.handler;

import com.github.professorSam.db.Database;
import com.github.professorSam.db.model.Player;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPostHandler implements Handler {

    Logger logger = LoggerFactory.getLogger("Login");

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String username = context.formParam("username");
        String groupname = context.formParam("groupname");
        String language = context.formParam("language");
        logger.info("Signup: " + username + " in " + groupname + " with language " + language);

        Player player = Database.createPlayerAndAddToGroup(username, groupname.toLowerCase(), language);
        context.result("PlayerID: " + player.id().toString() +
                " PlayerName: " + player.name()+
                " PlayerLang: " + player.nationality() +
                " GroupID: " + player.group().id().toString() +
                " GroupName: " + player.group().name() +
                " GroupQuest: " + player.group().quest());
    }
}
