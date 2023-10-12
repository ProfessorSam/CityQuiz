package com.github.professorSam.handler;

import com.github.professorSam.Main;
import com.github.professorSam.context.ErrorContext;
import com.github.professorSam.db.Database;
import com.github.professorSam.db.FileStorage;
import com.github.professorSam.db.model.Group;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadPhotosGetHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger("PictureDownload");

    @Override
    public void handle(@NotNull Context context) throws Exception {
        if(Main.getInstance().getAdminToken() != null){
            String token = context.queryParam("token");
            if(token == null || !token.equals(Main.getInstance().getAdminToken())){
                context.status(HttpStatus.UNAUTHORIZED);
                return;
            }
        }
        Map<Group, List<String>> groupsAndPictures = Database.getAllImages();
        if(groupsAndPictures == null){
            context.render("error.jte", Collections.singletonMap("context", new ErrorContext("Keine Bilder gefunden!")));
            return;
        }
        long startTime = Instant.now().toEpochMilli();
        File zipFile = File.createTempFile("FotosRalley", ".zip");
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))){
            for(Group group : groupsAndPictures.keySet()){
                List<String> pics = groupsAndPictures.get(group);
                for(int i = 0; i < pics.size(); i++){
                    String picID = pics.get(i);
                    String picNameFormatted = group.name() + "_" + (i + 1) + ".png";
                    InputStream inputStream = FileStorage.getFile(picID);
                    logger.info("Adding file " + picID + " for group " + group.name() + ". FileName: " + picNameFormatted);
                    if(inputStream == null){
                        logger.info("Pic " + picID + " not found!");
                        continue;
                    }
                    ZipEntry zipEntry = new ZipEntry(picNameFormatted);
                    zipOutputStream.putNextEntry(zipEntry);
                    inputStream = convertToPng(inputStream);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                    zipOutputStream.closeEntry();
                    inputStream.close();
                }
            }
        }
        InputStream zipInputStrean = new FileInputStream(zipFile);
        context.contentType("application/zip");
        context.header("Content-Disposition", "attachment; filename=ralleyFotos.zip");
        context.result(zipInputStrean);
        zipFile.delete();
        long endTime = Instant.now().toEpochMilli();
        logger.info("Images exported in " + (endTime - startTime) + "ms");
    }

    private InputStream convertToPng(InputStream inputStream){
        try {
            BufferedImage image = ImageIO.read(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            inputStream.close();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            logger.error("Can't convert Image to PNG: ", e);
            return inputStream;
        }
    }
}
