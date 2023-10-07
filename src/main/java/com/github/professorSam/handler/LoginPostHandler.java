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
        if(username == null || groupname == null || language == null){
            context.status(405);
            return;
        }
        logger.info("Signup: " + username + " in " + groupname + " with language " + language);
        Player player = Database.createPlayerAndAddToGroup(username, groupname.toLowerCase(), language);
        context.cookieStore().set("UserID", player.id().toString());
        context.redirect("/");
    }
}
