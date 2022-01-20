package de.dofe.ev3.rest;

import de.dofe.ev3.Robot;
import de.dofe.ev3.SvgPrinter;
import de.dofe.ev3.status.Status;
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

    private final Robot robot;

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
        } else if (session.getMethod().equals(Method.GET) && session.getUri().equals("/kill")) {
            System.exit(0);
        } else if (session.getMethod().equals(Method.GET) && session.getUri().equals("/zaxis")) {
            // to do: remove public access after testing
            robot.getZAxis().toggle();
            return newFixedLengthResponse(Response.Status.OK, mimeTypeHtml, "<html><body><h1>Z-Axis</h1><p>Z-Axis: " + robot.getZAxis().isActive() + "</p></body></html>");
        } else if (session.getMethod().equals(Method.GET) && session.getUri().equals("/debug")) {
            JSONObject sampleObject = new JSONObject();
            sampleObject.put("status", "ok");
            sampleObject.put("z (active)", robot.getYAxis().getSensor().isActive());
            sampleObject.put("x", robot.getCurrentPosition().getX());
            sampleObject.put("y", robot.getCurrentPosition().getY());
            return newFixedLengthResponse(Response.Status.OK, mimeTypeJson, sampleObject.toJSONString());
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

            String upload = new String(fileContent, StandardCharsets.UTF_8);
            Thread thread = new Thread(() -> robot.print(upload));
            thread.start();

            // prepare json response
            JSONObject sampleObject = new JSONObject();
            sampleObject.put("status", "ok");
            sampleObject.put("message", "File uploaded successfully. Read " + bytesRead + " bytes in " + iterations + " iterations.");
            sampleObject.put("length", streamLength + " Bytes");
            sampleObject.put(sContentType, session.getHeaders().get(sContentType));
            Response resp =  newFixedLengthResponse(Response.Status.OK, mimeTypeJson, sampleObject.toJSONString());
            resp.addHeader("Access-Control-Allow-Origin", "*");
            resp.addHeader("Access-Control-Allow-Methods", "POST; GET");

            return resp;

        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, mimeTypeHtml, e.getMessage());
        }
    }
}
