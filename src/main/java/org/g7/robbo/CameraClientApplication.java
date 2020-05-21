package org.g7.robbo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Orlov Diga
 */
@SpringBootConfiguration
@ComponentScan
@Slf4j
public class CameraClientApplication {

    private List<WebcamDevice> getUnixDevices() {
        List<WebcamDevice> devices = new ArrayList<WebcamDevice>();

        String[] command = {
                getCommand(),
                "-f",
                "avfoundation",
                "-list_devices",
                "true",
                "-hide_banner",
                "-i",
                "\"\""
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String videoSeperator = "video devices";
            String audioSeperator = "audio devices";
            String captureScreenDevice = "capture screen";
            String line;

            while ((line = bufferedReader.readLine()) != null) {

                if(line.toLowerCase().contains(videoSeperator)) {
                    continue;
                }

                if(line.toLowerCase().contains(captureScreenDevice)) {
                    break;
                }

                if(line.toLowerCase().contains(audioSeperator)) {
                    break;
                }

                String[] lineArray = line.split("] ");
                FFmpegCliDevice device = new FFmpegCliDevice(lineArray[2]);
                devices.add(device);
            }

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();

        }

        return devices;
    }

    public static void main(String[] args) {
        SpringApplication.run(CameraClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
