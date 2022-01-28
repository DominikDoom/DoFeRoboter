package de.dofe.ev3.rest;

import de.dofe.ev3.Main;
import de.dofe.ev3.status.Status;
import lombok.Data;

/**
 * This class is used to store the clients request.
 */
@Data
public class FileStore {
    /**
     * The type of the upload.
     */
    private String type;
    /**
     * The actual payload of the upload.
     */
    private String data;

    /**
     * Store the upload and send it to the EV3.
     */
    public void store() {
        float start = System.currentTimeMillis();
        Main.getRobot().setStatus(Status.PRINTING);
        Main.getRobot().print(data);

        float end = System.currentTimeMillis();
        Main.getStatsHandler().addPrint(end - start, Main.getRobot().getPathsParsed());
        Main.getRobot().setPathsParsed(0);

        Main.getRobot().setStatus(Status.READY);
    }
}
