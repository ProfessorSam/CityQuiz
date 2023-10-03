package com.github.professorSam.handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class LoginGetHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ctx.render("login.jte");
    }
}
