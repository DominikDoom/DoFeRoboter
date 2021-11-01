package de.dofe.ev3.reifen;

import de.dofe.ev3.IUebersetzung;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

public class Reifensatz implements IUebersetzung {

    @Getter
    private final ArrayList<Reifen> reifen = new ArrayList<>();

    public Reifensatz(Reifen... reifen) {
        this.reifen.addAll(Arrays.asList(reifen));
    }

    @Override
    public double getUebersetzungsverhaeltnis() {
        if (this.reifen.size() == 0)
            return -1;
        else if (this.reifen.size() == 1)
            return 1;

        double result = 0;
        for (int i = reifen.size() - 1; i >= 0; i--) {
            if (result == 0)
                result = reifen.get(i).getDurchmesser();
            else
                result /= reifen.get(i).getDurchmesser();
        }
        return result;
    }

    @Override
    public boolean isAntriebsumkehrung() {
        return this.reifen.size() % 2 == 0;
    }
}
