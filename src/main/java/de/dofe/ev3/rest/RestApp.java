package de.dofe.ev3.rest;

import de.dofe.ev3.Robot;
import de.dofe.ev3.SvgPrinter;
import fi.iki.elonen.NanoHTTPD;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RestApp extends NanoHTTPD {

    private static final String mimeTypeHtml = "text/html";
    private static final String mimeTypeJson = "application/json";
    private static final String sContentLength = "content-length";
    private static final String sContentType = "content-type";

    private final SvgPrinter robot;

    public RestApp(Robot robot) throws IOException {
        super(8080);
        this.robot = robot;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Running...");
    }

    @SneakyThrows
    @Override
    public Response serve(IHTTPSession session) {

        if (session.getMethod().equals(Method.POST) && session.getUri().equals("/upload") && session.getHeaders().get(sContentLength) != null) {
            return postUpload(session);
        }

        return newFixedLengthResponse(Response.Status.BAD_REQUEST, mimeTypeHtml, "Invalid request");
    }

    @SuppressWarnings("unchecked")
    public Response postUpload(IHTTPSession session) {
        int streamLength = Integer.parseInt(session.getHeaders().get(sContentLength));
        byte[] fileContent = new byte[streamLength];
        try {
            // Read the file content
            InputStream input = session.getInputStream();
            int bytesRead = 0;
            int iterations = 0;
            while (bytesRead < streamLength) {
                int thisRead = input.read(fileContent, bytesRead, streamLength - bytesRead);
                bytesRead += thisRead;
                iterations++;
            }

            // write uploaded file to disk
            String upload = new String(fileContent, StandardCharsets.UTF_8);

            // call print method
            robot.print(upload);

            // prepare json response
            JSONObject sampleObject = new JSONObject();
            sampleObject.put("status", "ok");
            sampleObject.put("message", "File uploaded successfully. Read " + bytesRead + " bytes in " + iterations + " iterations.");
            sampleObject.put("length", streamLength + " Bytes");
            sampleObject.put(sContentType, session.getHeaders().get(sContentType));
            return newFixedLengthResponse(Response.Status.OK, mimeTypeJson, sampleObject.toJSONString());
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, mimeTypeHtml, e.getMessage());
        }
    }
}
