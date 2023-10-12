package com.github.professorSam.handler;

import com.github.professorSam.db.FileStorage;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ImageGetHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger("ImageGet");

    @Override
    public void handle(@NotNull Context context) throws IOException {
        String id = context.queryParam("id");
        if(id == null){
            context.status(HttpStatus.NOT_FOUND_404);
            return;
        }
        InputStream stream = FileStorage.getFile(id);
        if(stream == null){
            logger.info("Request for invalid file: " + id);
            context.status(HttpStatus.NOT_FOUND_404);
            return;
        }
        context.result(stream);
    }
}
