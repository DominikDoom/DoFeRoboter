package de.dofe.ev3.rest;

import lombok.Getter;

import java.io.*;

public class StatsHandler implements Serializable {

    @Getter
    private int totalPrints = 0;
    @Getter
    private float averagePrintTime = 0;

    @Getter
    private int pathsParsed = 0;

    // initialize values
    public StatsHandler() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("stats.dat"))) {
            StatsHandler stats = (StatsHandler) ois.readObject();
            totalPrints = stats.getTotalPrints();
            averagePrintTime = stats.getAveragePrintTime();
            pathsParsed = stats.getPathsParsed();
        } catch (Exception e) {
            System.out.println("No stats file found");
        }
    }

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("stats.dat", false))) {
            oos.writeObject(this);
        } catch (Exception e) {
            System.out.println("Could not save stats");
        }
    }

    public void addPrint(float printTime, int pathsParsed) {
        totalPrints++;
        this.pathsParsed += pathsParsed;

        if (averagePrintTime == 0) {
            averagePrintTime = printTime;
        } else {
            averagePrintTime = (averagePrintTime + printTime) / 2;
        }

        // write to file to persist stats
        save();
    }
}
