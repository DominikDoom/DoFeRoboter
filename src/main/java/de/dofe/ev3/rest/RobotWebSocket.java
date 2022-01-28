package de.dofe.ev3.rest;

import com.google.gson.Gson;
import de.dofe.ev3.status.Status;
import de.dofe.ev3.status.StatusObserver;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;

import java.net.InetSocketAddress;
import java.util.Collection;


/**
 * A simple WebSocketServer
 */
@SuppressWarnings("unchecked")
public class RobotWebSocket extends WebSocketServer implements StatusObserver {

    private Status lastStatus = Status.UNDEFINED;

    public RobotWebSocket( int port ) {
        super( new InetSocketAddress( port ) );
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        log( "Opened connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress() );
        sendStatusUpdate(lastStatus);
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        log( "Closed connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " (" + code + "): ");
        Status statusBefore = lastStatus;
        sendStatusUpdate(Status.UNDEFINED);
        lastStatus = statusBefore;
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        log( "From " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " received: " + message );

        // Map the message to a Status
        Gson gson = new Gson();
        FileStore fileStore = gson.fromJson(message, FileStore.class);

        // Send response to client
        JSONObject json = new JSONObject();
        json.put( "status", "ok" );
        json.put("type", "upload");
        json.put("timestamp", System.currentTimeMillis());
        conn.send( json.toJSONString() );

        // Print the file store asynchronously
        if (fileStore.getType().equals("upload")) {
            Thread thread = new Thread(fileStore::store);
            thread.start();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex ) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        log( "Started WebSocket Server on port " + getPort() );
    }

    /**
     * Pipelines the current state of the EV3 to all connected clients.
     */
    private void sendStatusUpdate(Status status) {
        lastStatus = status;
        JSONObject json = new JSONObject();
        json.put( "type", "status" );
        json.put( "status", status.toString() );
        json.put("timestamp", System.currentTimeMillis());
        sendToAll( json.toJSONString() );
    }

    /**
     * Sends <var>text</var> to all currently connected WebSocket clients.
     *
     * @param text
     *            The String to send across the network.
     */
    public void sendToAll( String text ) {
        Collection<WebSocket> con = getConnections();
        synchronized ( con ) {
            for( WebSocket c : con ) {
                c.send( text );
            }
        }
    }

    /**
     * The WebSocket Prefix.
     */
    private static final String PREFIX = "[WEBSOCKET] ";

    /**
     * Logs a message to the console.
     *
     * @param message
     *            The message to log.
     */
    private void log( String message ) {
        System.out.println( PREFIX + message );
    }

    @Override
    public void update(Status status) {
        sendStatusUpdate(status);
    }
}
