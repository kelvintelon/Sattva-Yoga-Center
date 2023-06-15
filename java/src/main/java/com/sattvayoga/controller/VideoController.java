package com.sattvayoga.controller;


import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class VideoController {

    @GetMapping(value = "/get-file")
    @ResponseBody
    public void getFile(@RequestHeader(value = "Range", required = false) String rangeHeader, @RequestParam String fileName, HttpServletResponse response) throws IOException {
        // https://stackoverflow.com/questions/8841407/grabbing-the-range-values-from-the-httpheader
        // https://stackoverflow.com/a/65526884 second or third solution


        // You are directly writing the response to the HttpServletResponse object using its output stream.
        // You read the video file using a RandomAccessFile and seek to the appropriate position based on the range requested.
        // Then, you write the file data to the output stream in chunks using a buffer.

        long rangeStart = 0;
        long rangeEnd;
        // 09.18.20 - Friday_
        String filePathString = "src/main/java/Videos/" + fileName;

        OutputStream os = response.getOutputStream();

        Path filePath = Paths.get("src/main/java/Videos/" + fileName);

        Long fileSize = Files.size(filePath);

        byte[] buffer = new byte[1024];

        RandomAccessFile file = new RandomAccessFile(filePathString, "r");

        try (file) {
            if (rangeHeader == null) {
                response.setHeader("Content-Type", "video/mp4");
                response.setHeader("Content-Length", fileSize.toString());
                response.setStatus(HttpStatus.OK.value());
                long pos = rangeStart;
                file.seek(pos);
                while (pos < fileSize - 1) {
                    file.read(buffer);
                    os.write(buffer);
                    pos += buffer.length;
                }
                os.flush();
                return;
            }

            String[] ranges = rangeHeader.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }

            String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
            response.setHeader("Content-Type", "video/mp4");
            response.setHeader("Content-Length", contentLength);
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize);
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
            long pos = rangeStart;
            file.seek(pos);
            while (pos < rangeEnd) {
                file.read(buffer);
                os.write(buffer);
                pos += buffer.length;
            }
            os.flush();

        }


    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/get-all-filenames")
    public List<String> getAllFileNames() throws IOException {
        File folder = new File("src/main/java/Videos");

        File[] listOfFiles = folder.listFiles();

        List<String> allFileNames = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            allFileNames.add(listOfFiles[i].getName());
        }

        return allFileNames;

    }



}
