package com.github.professorSam.db;

import io.minio.*;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileStorage {

    private static final Logger logger = LoggerFactory.getLogger("FileStorage");

    public static final String BUCKET_NAME = "cityquizimgs";
    private static final String ENDPOINT = "http://minio:9000";
    private static final String ACCESS_KEY = System.getenv("MINIO_ACCESS_KEY");
    private static final String SECRET_KEY = System.getenv("MINIO_SECRET_KEY");
    private static MinioClient minioClient;

    public static boolean init(){
        minioClient = MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();
        try {
            if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())){
                logger.info("Bucket " + BUCKET_NAME + " does not exists. Creating...");
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
                logger.info("Bucket created!");
            }
            return true;
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Can't create bucket!", e);
            return false;
        }
    }

    public static void uploadFile(InputStream fileInput, String id){
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(id)
                            .stream(fileInput, -1, PutObjectArgs.MIN_MULTIPART_SIZE)
                            .build()
            );
            logger.info("Uploaded " + id);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Can't create bucket!", e);
        }
    }

}
