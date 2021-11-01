package de.dofe.ev3.zahnrad;

import de.dofe.ev3.IUebersetzung;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

public class Zahnradsatz implements IUebersetzung {

    @Getter
    private final ArrayList<Zahnrad> zahnraeder = new ArrayList<>();

    public Zahnradsatz(Zahnrad... zahnraeder) {
        this.zahnraeder.addAll(Arrays.asList(zahnraeder));
    }

    @Override
    public double getUebersetzungsverhaeltnis() {
        if (this.zahnraeder.size() == 0)
            return -1;
        else if (this.zahnraeder.size() == 1)
            return 1;

        double result = 0;
        for (int i = zahnraeder.size() - 1; i >= 0; i--) {
            if (result == 0)
                result = zahnraeder.get(i).getSize();
            else
                result /= zahnraeder.get(i).getSize();
        }
        return result;
    }

    @Override
    public boolean isAntriebsumkehrung() {
        return this.zahnraeder.size() % 2 == 0;
    }
}
