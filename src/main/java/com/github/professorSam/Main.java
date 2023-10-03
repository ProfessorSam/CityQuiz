package com.github.professorSam;

import com.github.professorSam.handler.IndexGetHandler;
import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.javalin.Javalin;

import java.nio.file.Path;

public class Main {

    private final Javalin webserver;
    private final TemplateEngine templateEngine;
    private static Main INSTANCE;


    private Main(){
        CodeResolver codeResolver = new DirectoryCodeResolver(Path.of("jte"));
        templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        webserver = Javalin.create()
                .get("/", new IndexGetHandler())
                .start(80);
    }

    public static void main(String[] args) {
        INSTANCE = new Main();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            INSTANCE.getWebserver().stop();
        }));
        while(true){

        }
    }

    public static Main getINSTANCE() {
        return INSTANCE;
    }

    public Javalin getWebserver() {
        return webserver;
    }
}