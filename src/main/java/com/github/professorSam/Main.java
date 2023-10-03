package com.github.professorSam;

import com.github.professorSam.handler.IndexGetHandler;
import com.github.professorSam.handler.LoginGetHandler;
import com.github.professorSam.handler.LoginPostHandler;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

public class Main {

    private final Javalin webserver;
    private static Main INSTANCE;


    private Main(){
        JavalinJte.init();
        webserver = Javalin.create()
                .get("/", new IndexGetHandler())
                .get("/login", new LoginGetHandler())
                .post("/login", new LoginPostHandler())
                .start(80);
    }

    public static void main(String[] args) {
        INSTANCE = new Main();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> INSTANCE.getWebserver().stop()));
        while(true){

        }
    }

    public static Main getInstance() {
        return INSTANCE;
    }

    public Javalin getWebserver() {
        return webserver;
    }
}