package com.github.professorSam.handler;

import com.github.professorSam.Main;
import com.github.professorSam.db.Database;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;

public class AdminSetTimePostHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger("EndTimePost");
    @Override
    public void handle(@NotNull Context context) throws Exception {
        if(Main.getInstance().getAdminToken() != null){
            String token = context.queryParam("token");
            if(token == null || !token.equals(Main.getInstance().getAdminToken())){
                context.status(HttpStatus.UNAUTHORIZED);
                return;
            }
        }
        String time = context.formParam("endtime");
        if(time == null){
            context.redirect("/admin");
            return;
        }
        LocalTime localTime = LocalTime.parse(time);
        LocalDateTime localDateTime = LocalDate.now().atTime(localTime);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Berlin"));
        Instant instant = zonedDateTime.toInstant();
        Database.setEndTime(instant);
        logger.info("New time: " + time);
        context.redirect("/admin");
    }
}
