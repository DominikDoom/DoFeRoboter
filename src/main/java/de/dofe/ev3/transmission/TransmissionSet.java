package de.dofe.ev3.transmission;

import de.dofe.ev3.transmission.unit.ITransmissionUnit;

import java.util.ArrayList;
import java.util.Arrays;

public class TransmissionSet extends ArrayList<ITransmissionUnit> implements ITransmission {

    public TransmissionSet(ITransmissionUnit... units) {
        this.addAll(Arrays.asList(units));
    }

    @Override
    public double getRatio() {
        if (this.size() < 2)
            return 1;

        return this.get(this.size() - 1).getSize() / this.get(0).getSize();
    }

    @Override
    public boolean isReversing() {
        return this.size() % 2 == 0;
    }
}
