package com.github.professorSam;

import com.github.professorSam.db.Database;
import com.github.professorSam.db.FileStorage;
import com.github.professorSam.handler.*;
import com.github.professorSam.quest.Quest;
import com.github.professorSam.quest.QuestFactory;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {

    private final Javalin webserver;
    private List<Quest> quests;
    private static Main INSTANCE;
    private static final Logger logger = LoggerFactory.getLogger("Main");


    private Main(boolean dev){
        logger.info("Starting! Loading quests...");
        try {
            quests = QuestFactory.createQuestsFromJson();
        } catch (IOException e) {
            logger.error("Can't read or find quests.json");
            System.exit(-1);
        }
        logger.info("Questes loaded. Loading templating engine...");
        JavalinJte.init(createTemplateEngine(dev));
        logger.info("Template engine loaded. Loading file storage...");
        if(!FileStorage.init()){
            logger.warn("Can't load minio file storage! Exiting...");
            System.exit(-1);
        }
        logger.info("File storage loaded! Testing DB connection...");
        if(!Database.testConnection()){
            logger.warn("Database connection is not valid! Exit application...");
            System.exit(-1);
        }
        logger.info("DB connection valid. Creating tables...");
        Database.setupTables();
        logger.info("Tables created. Starting webserver...");
        webserver = Javalin.create()
                .get("/", new IndexGetHandler())
                .get("/login", new LoginGetHandler())
                .post("/login", new LoginPostHandler())
                .get("/questoverview", new QuestOverviewGetHandler())
                .get("/questview", new QuestViewGetHandler())
                .post("/questview", new QuestViewPostHandler())
                .start(80);
    }

    public static void main(String[] args) {
        List<String> arguments = List.of(args);
        boolean dev = arguments.contains("--dev");
        INSTANCE = new Main(dev);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            INSTANCE.getWebserver().stop();
            Database.close();
        }));
        while(true){

        }
    }

    private TemplateEngine createTemplateEngine(boolean isDevSystem) {
        if (isDevSystem) {
            DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src", "main", "jte"));
            return TemplateEngine.create(codeResolver, ContentType.Html);
        } else {
            return TemplateEngine.createPrecompiled(ContentType.Html);
        }
    }

    public static Main getInstance() {
        return INSTANCE;
    }

    public Javalin getWebserver() {
        return webserver;
    }
    public List<Quest> getQuests() {
        return quests;
    }
}