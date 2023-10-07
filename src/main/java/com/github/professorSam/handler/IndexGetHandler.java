package com.github.professorSam.handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class IndexGetHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger("IndexGet");

    @Override
    public void handle(@NotNull Context context) throws Exception {
        long start = Instant.now().toEpochMilli();
        if(context.cookieStore().get("UserID") != null && !context.cookieStore().get("UserID").equals("null")){
            context.redirect("/questoverview");
            long end = Instant.now().toEpochMilli();
            log.info("From " + context.ip() + " in " + (end - start) + "ms (already logged in)");
            return;
        }
        context.render("index.jte");
        long end = Instant.now().toEpochMilli();
        log.info("From " + context.ip() + " in " + (end - start) + "ms");
    }
}
