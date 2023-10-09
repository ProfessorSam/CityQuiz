package com.github.professorSam.handler;

import com.github.professorSam.db.Database;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class CurrentQuestGetHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) {
        String id = context.queryParam("id");
        if(id == null){
            context.status(HttpStatus.NOT_FOUND);
            return;
        }
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e){
            context.status(HttpStatus.NOT_FOUND);
            return;
        }
        context.result("{\"value\": "+ Database.getCurrentQuestByGroupID(id) + "}");
    }
}
