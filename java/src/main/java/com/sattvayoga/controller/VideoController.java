package com.sattvayoga.controller;


import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class VideoController {

    @GetMapping(
            value = "/get-file",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity getFile(@RequestHeader HttpHeaders headers) throws IOException {
        // https://stackoverflow.com/questions/8841407/grabbing-the-range-values-from-the-httpheader
      List<String> startList = headers.get("rangestartvalue");
      String startString = startList.get(0);
      int startInt = Integer.parseInt(startString);
        List<String> endList = headers.get("rangeendvalue");
        String endString = endList.get(0);
        int endInt = Integer.parseInt(endString);

        String range = "";
        if (range.equals("")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "video/" + "mp4")
                    .header("Content-Length", String.valueOf(endInt - 1))
                    .body(readByteRange("09.18.20 - Friday_.mp4", startInt,endInt)); // Read the object and convert it as bytes
        } else {
            return null;
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
        // intputstream.read(b, offset, len)       Reads up to len bytes of data from this input stream into an array of bytes. If len is not zero, the method blocks until some input is available; otherwise, no bytes are read and 0 is returned.
        //Params:
        //b – the buffer into which the data is read.
        //off – the start offset in the destination array b
        //len – the maximum number of bytes read.
        //Returns:
        //the total number of bytes read into the buffer, or -1 if there is no more data because the end of the file has been reached.

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            bufferedOutputStream.write(data, 0, nRead);
        }
        bufferedOutputStream.flush();
        byte[] result = new byte[(int) (end - start)];

        // System.arraycopy() method copies a source array from a specific beginning position to the destination array from the mentioned position.

        System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, (int) (end - start));

        return result;
    }

}
