package com.sattvayoga.controller;


import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class VideoController {

    @GetMapping(
            value = "/get-file",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity getFile(@RequestParam(defaultValue = "0-3000")  String range,
                                  @RequestParam(defaultValue = "0")  int rangeStart) throws IOException {
        if (range == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "video/" + "mp4")
                    .header("Content-Length", String.valueOf(2000 - 1))
                    .body(readByteRange("09.18.20 - Friday_.mp4", 0,2000)); // Read the object and convert it as bytes
        } else {
            return null;
        }
    }

    public byte[] readByteRange(String filename, long start, long end) throws IOException {

        FileInputStream inputStream = new FileInputStream("java/src/main/java/Videos/" + filename);
        ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            bufferedOutputStream.write(data, 0, nRead);
        }
        bufferedOutputStream.flush();
        byte[] result = new byte[(int) (end - start)];
        System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, (int) (end - start));
        return result;
    }

}
