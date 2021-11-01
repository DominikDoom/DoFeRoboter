package de.dofe.ev3.transmission;

import de.dofe.ev3.transmission.unit.ITransmissionUnit;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Provides transmission calculations for an arbitrary
 * number of {@link ITransmissionUnit} units.
 */
public class TransmissionSet extends ArrayList<ITransmissionUnit> implements ITransmission {

    /**
     * Initializes a {@link TransmissionSet} with all provided units.
     *
     * @param units The collection of units to add to the set.
     */
    public TransmissionSet(ITransmissionUnit... units) {
        this.addAll(Arrays.asList(units));
    }

    /**
     * This method calculates the transmission ratio of the set.
     * <p>
     * It assumes units are mounted in a linear chain as opposed
     * to transmission axes which simplifies the calculation.
     * In this case, only the first and last unit in the chain are relevant.
     *
     * @return The transmission ratio.
     */
    @Override
    public double getRatio() {
        if (this.size() < 2)
            return 1;

        return this.get(this.size() - 1).getSize() / this.get(0).getSize();
    }

    /**
     * In a linear chain, the rotation direction reverses
     * for every added unit, resulting in the rotation of the
     * last gear reversing if an even number of units is in the set.
     */
    @Override
    public boolean isReversing() {
        return this.size() % 2 == 0;
    }
}
