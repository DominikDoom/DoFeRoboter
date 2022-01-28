package de.dofe.ev3.rest;

import de.dofe.ev3.Main;
import de.dofe.ev3.Robot;
import fi.iki.elonen.NanoHTTPD;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;

import java.io.IOException;

public class RestApp extends NanoHTTPD {

    /** Mime types for response. */
    private static final String mimeTypeHtml = "text/html";
    private static final String mimeTypeJson = "application/json";

    /** The robot instance */
    private final Robot robot;

    /**
     * Constructor for the RestApp class
     * @throws IOException if the server cannot be started
     */
    public RestApp() throws IOException {
        super(8080);
        this.robot = Main.getRobot();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Running...");
    }

    /**
     * Handles the request and returns the response
     *
     * <ul>
     *     <li><b>GET /kill</b> -> kills the server</li>
     *     <li><b>GET /zaxis</b> -> toggles z-axis and returns the current state</li>
     *     <li><b>GET /debug</b> -> displays debug information including the current states</li>
     * </ul>
     *
     * @param session the session
     * @return the response
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    public Response serve(IHTTPSession session) {

        // kill the robot if the user wants to
        if (session.getMethod().equals(Method.GET) && session.getUri().equals("/kill")) {
            System.exit(0);
        }
        // toggle the z axis
        else if (session.getMethod().equals(Method.GET) && session.getUri().equals("/zaxis")) {
            // to do: remove public access after testing
            robot.getZAxis().toggle();
            return newFixedLengthResponse(Response.Status.OK, mimeTypeHtml, "<html><body><h1>Z-Axis</h1><p>Z-Axis: " + robot.getZAxis().isActive() + "</p></body></html>");
        }
        // debugging endpoint, displaying little information about the robots states
        else if (session.getMethod().equals(Method.GET) && session.getUri().equals("/debug")) {
            JSONObject sampleObject = new JSONObject();
            sampleObject.put("status", "ok");
            sampleObject.put("z (active)", robot.getYAxis().getSensor().isActive());
            sampleObject.put("x", robot.getCurrentPosition().getX());
            sampleObject.put("y", robot.getCurrentPosition().getY());
            return newFixedLengthResponse(Response.Status.OK, mimeTypeJson, sampleObject.toJSONString());
        }
        // stats endpoint
        else if (session.getMethod().equals(Method.GET) && session.getUri().equals("/stats")) {
            JSONObject sampleObject = new JSONObject();
            sampleObject.put("totalPrints", Main.getStatsHandler().getTotalPrints());
            sampleObject.put("avgPrintTime", Main.getStatsHandler().getAveragePrintTime());
            sampleObject.put("pathsParsed", Main.getStatsHandler().getPathsParsed());
            Response response = newFixedLengthResponse(Response.Status.OK, mimeTypeJson, sampleObject.toJSONString());
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            return response;
        }
        // returned if the requested endpoint is not found
        return newFixedLengthResponse(Response.Status.BAD_REQUEST, mimeTypeHtml, "Invalid request");
    }
}
