package com.github.professorSam.handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class IndexGetHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        context.result("<h1>Hallo Welt!</h1>");
    }
}
