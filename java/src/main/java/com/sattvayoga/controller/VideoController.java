package com.sattvayoga.controller;


import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class VideoController {

    @GetMapping(
            value = "/get-file",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity getFile(@RequestHeader HttpHeaders headers) throws IOException {
        // https://stackoverflow.com/questions/8841407/grabbing-the-range-values-from-the-httpheader

        List<HttpRange> startList = headers.getRange();

        File f = new File("src/main/java/Videos/09.18.20 - Friday_.mp4");

        long startLong = startList.get(0).getRangeStart(f.length());

        long endLong = startList.get(0).getRangeEnd(f.length());


        if (true) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .header("Content-Type", "video/" + "mp4")
                    .header("Content-Length", String.valueOf(endLong - (startLong + 1)))
                    .header("Accept-Ranges", "bytes")
                    .body(readByteRange("09.18.20 - Friday_.mp4", startLong, endLong)); // Read the object and convert it as bytes
        } else {
            return (ResponseEntity) ResponseEntity.status(HttpStatus.BAD_REQUEST);
        }
    }

    // https://stackoverflow.com/questions/31591166/load-video-from-inputstream-using-java-and-video-js
    // https://stackoverflow.com/a/16157075
    //

    public byte[] readByteRange(String filename, long start, long end) throws IOException {

        FileInputStream inputStream = new FileInputStream("src/main/java/Videos/" + filename);
        ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        // intputstream.read(b, offset, len)       Reads up to len bytes of data from this input stream into an array of bytes.
        // If len is not zero, the method blocks until some input is available; otherwise, no bytes are read and 0 is returned.
        //Params:
        //b – the buffer into which the data is read.
        //off – the start offset in the destination array b
        //len – the maximum number of bytes read.
        //Returns:
        //the total number of bytes read into the buffer, or -1 if there is no more data because the end of the file has been reached.
        byte[] result = new byte[(int) (end - start)];

        inputStream.read(result,0, (int) end);


//        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//            bufferedOutputStream.write(data, 0, nRead);
//        }
//
//        bufferedOutputStream.flush();
//
//
//        // System.arraycopy() method copies a source array from a specific beginning position to the destination array from the mentioned position.
//
//        System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, (int) (end - start));

        return result;
    }

}
