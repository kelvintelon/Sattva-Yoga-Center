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
        String filePathString = "src/main/java/Videos/" + fileName + ".mp4";

        OutputStream os = response.getOutputStream();

        Path filePath = Paths.get("src/main/java/Videos/" + fileName + ".mp4");

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

//    @GetMapping(
//            value = "/get-file"
//    )
//    @ResponseBody
//    public ResponseEntity<StreamingResponseBody>  getFile(@RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
//        // https://stackoverflow.com/questions/8841407/grabbing-the-range-values-from-the-httpheader
//        // https://stackoverflow.com/a/65526884 second or third solution
//
//
//        StreamingResponseBody responseStream;
//
//        long rangeStart = 0;
//        long rangeEnd;
//
//        String filePathString = "src/main/java/Videos/09.18.20 - Friday_.mp4";
//
//        final HttpHeaders responseHeaders = new HttpHeaders();
//
//        Path filePath = Paths.get("src/main/java/Videos/09.18.20 - Friday_.mp4");
//
//        Long fileSize = Files.size(filePath);
//
//        byte[] buffer = new byte[1024];
//
//
//        if (rangeHeader == null) {
//            responseHeaders.add("Content-Type", "video/mp4");
//            responseHeaders.add("Content-Length", fileSize.toString());
//            responseStream = os -> {
//                RandomAccessFile file = new RandomAccessFile(filePathString, "r");
//                try (file) {
//                    long pos = 0;
//                    file.seek(pos);
//                    while (pos < fileSize - 1) {
//                        file.read(buffer);
//                        os.write(buffer);
//                        pos += buffer.length;
//                    }
//                    os.flush();
//                } catch (Exception e) {}
//            };
//            return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.OK);
//        }
//
//        String[] ranges = rangeHeader.split("-");
//         rangeStart = Long.parseLong(ranges[0].substring(6));
//
//        if (ranges.length > 1) {
//            rangeEnd = Long.parseLong(ranges[1]);
//        } else {
//            rangeEnd = fileSize - 1;
//        }
//        if (fileSize < rangeEnd) {
//            rangeEnd = fileSize - 1;
//        }
//
//        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
//        responseHeaders.add("Content-Type", "video/mp4");
//        responseHeaders.add("Content-Length", contentLength);
//        responseHeaders.add("Accept-Ranges", "bytes");
//        responseHeaders.add("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize);
//        final Long _rangeEnd = rangeEnd;
//        long finalRangeStart = rangeStart;
//        responseStream = os -> {
//            RandomAccessFile file = new RandomAccessFile(filePathString, "r");
//            try (file) {
//                long pos = finalRangeStart;
//                file.seek(pos);
//                while (pos < _rangeEnd) {
//                    file.read(buffer);
//                    os.write(buffer);
//                    pos += buffer.length;
//                }
//                os.flush();
//            } catch (Exception e) {}
//        };
//        return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
//
//
//
//    }


    // https://stackoverflow.com/questions/31591166/load-video-from-inputstream-using-java-and-video-js
    // https://stackoverflow.com/a/16157075
    //

//    public byte[] readByteRange(String filename, long start, long end) throws IOException {
//
//        FileInputStream inputStream = new FileInputStream("src/main/java/Videos/" + filename);
//        ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
//        byte[] data = new byte[1024];
//        int nRead;
//
//        // intputstream.read(b, offset, len)       Reads up to len bytes of data from this input stream into an array of bytes.
//        // If len is not zero, the method blocks until some input is available; otherwise, no bytes are read and 0 is returned.
//        //Params:
//        //b – the buffer into which the data is read.
//        //off – the start offset in the destination array b
//        //len – the maximum number of bytes read.
//        //Returns:
//        //the total number of bytes read into the buffer, or -1 if there is no more data because the end of the file has been reached.
//        byte[] result = new byte[(int) (end - start)];
//
//        inputStream.read(result,0, (int) end);

//
//        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//            bufferedOutputStream.write(data, 0, nRead);
//        }
//
////        bufferedOutputStream.flush();
//
//
//        // System.arraycopy() method copies a source array from a specific beginning position to the destination array from the mentioned position.
//
//        System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, (int) (end - start));

//        return result;
//    }

    //        long contentLength = videoFileStream.available();

//        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {

//        final long start = startList.get(0).getRangeStart(videoFile.length());
//
//        long end = startList.get(0).getRangeEnd(videoFile.length());
//
//            if (end == Long.MAX_VALUE) {
//                end = contentLength - 1;
//            }
//
//            final long[] rangeLength = {end - start + 1};
//
//
//
//            return ResponseEntity.status(206)
//                    .header("Content-Range", "bytes " + start + "-" + end + "/" + contentLength)
//                    .header("Content-Length", String.valueOf(rangeLength[0]))
//                    .header("Accept-Ranges", "bytes")
//                    .body(outputStream -> {
//                        try {
//                            videoFileStream.skip(start);
//                            byte[] buffer = new byte[1024];
//                            int bytesRead;
//                            while (rangeLength[0] > 0 && (bytesRead = videoFileStream.read(buffer)) != -1) {
//                                outputStream.write(buffer, 0, (int) Math.min(rangeLength[0], bytesRead));
//                                rangeLength[0] -= bytesRead;
//                            }
//                        } finally {
//                            videoFileStream.close();
//                        }
//                    });
//        }
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(outputStream -> Files.copy(videoFile.toPath(), outputStream));

    //TODO: CASE 2

//
//        long startLong = startList.get(0).getRangeStart(f.length());
//
//        long endLong = startList.get(0).getRangeEnd(f.length());
//
////        if (endLong == Long.MAX_VALUE) {
////            endLong = f.length() - 1;
////        }
//
//        long rangeLength = endLong - startLong + 1;
//
//        long finalEndLong = endLong;
//
//        if (true) {
//
//            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                    .header("Content-Range", "bytes " + startLong + "-" + endLong + "/" + f.length())
//                    .header("Content-Type", "video/" + "mp4")
//                    .header("Content-Length", String.valueOf(f.length()))
//                    .header("Accept-Ranges", "bytes")
//                    .body(outputStream -> readByteRange("09.18.20 - Friday_.mp4", startLong, endLong)); // Read the object and convert it as bytes
//        } else {
//            return (ResponseEntity) ResponseEntity.status(HttpStatus.BAD_REQUEST);
//        }


    //TODO: CASE 3

//        List<String> startList = headers.get("rangestartvalue");
//        String startString = startList.get(0);
//        long startLong = Long.parseLong(startString);
//
//        List<String> endList = headers.get("rangeendvalue");
//        String endString = endList.get(0);
//        long endLong = Long.parseLong(endString);

    // !startString.equals("") && !endString.equals("")

//        if (true) {
//            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                    .header("Content-Type", "video/" + "mp4")
//                    .header("Content-Length", String.valueOf(endLong - (startLong + 1)))
//                    .header("Accept-Ranges", "bytes")
//                    .body(outputStream -> readByteRange("09.18.20 - Friday_.mp4", startLong, endLong)); // Read the object and convert it as bytes
//        } else {
//            return (ResponseEntity) ResponseEntity.status(HttpStatus.BAD_REQUEST);
//        }

}
