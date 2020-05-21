package org.g7.robbo.client;

import lombok.extern.slf4j.Slf4j;
import org.g7.robbo.camera.RPiCamera;
import org.g7.robbo.camera.enums.AWB;
import org.g7.robbo.camera.enums.DRC;
import org.g7.robbo.camera.enums.Encoding;
import org.g7.robbo.camera.exceptions.FailedToRunRaspistillException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author Orlov Diga
 */
@Slf4j
@Service
@EnableScheduling
public class CameraRestClient {

    private static final String SAVE_DIR = "/home/ubuntu/";

    private final RPiCamera piCamera;
    private final RestTemplate rest;
    private final String URL = "http://192.168.0.129:8080/camera";

    @Autowired
    public CameraRestClient(RestTemplate rest, RPiCamera rPiCamera) {
        this.rest = rest;
        this.piCamera = rPiCamera;

        log.info("Init piCamera successful.");
    }

    @PostConstruct
    public void init() {
        log.info("Init method Camera Adapter start...");
        piCamera.setAWB(AWB.AUTO)       // Change Automatic White Balance setting to automatic
                .setDRC(DRC.OFF)            // Turn off Dynamic Range Compression
                .setContrast(100)           // Set maximum contrast
                .setSharpness(100)          // Set maximum sharpness
                .setQuality(100)            // Set maximum quality
                .setTimeout(1000)           // Wait 1 second to take the image
                .turnOnPreview();          // Turn on image preview
                //.setEncoding(Encoding.PNG);  // Turn on image preview
    }

    @Scheduled(fixedRate = 2000)
    public void sendPhoto() throws IOException, InterruptedException {
        log.info("Send photo start...");

        String namePhoto = "/home/ubuntu/photos/photo"+ LocalDateTime.now() + ".jpg";

        BufferedImage image = piCamera.takeBufferedStill(); // Take image and store in BufferedImage
        //BufferedImage bImage = ImageIO.read(image);
        ImageIO.write(image, "jpg", new File(namePhoto));

        log.info("take image: {}", image);

        //rest.put(URL, buffImg);
        //rest.postForLocation(URL, buffImg);
       // rest.postForObject(URL, image, File.class);
        log.info("Send photo finish");
    }
}
