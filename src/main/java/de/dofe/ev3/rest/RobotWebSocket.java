package de.dofe.ev3.rest;

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
public class RobotWebSocket extends WebSocketServer implements StatusObserver {

    public RobotWebSocket( int port ) {
        super( new InetSocketAddress( port ) );
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        this.sendToAll( "New connection established: " + handshake.getResourceDescriptor() );
        log( "Opened connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress() );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        this.sendToAll( "EV3 Connection closed: " + conn.getRemoteSocketAddress().getAddress().getHostAddress() );
        log( "Closed connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " (" + code + "): ");
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        this.sendToAll( message );
        log( "From " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " received: " + message );
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
        JSONObject json = new JSONObject();
        json.put( "status", status );
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
