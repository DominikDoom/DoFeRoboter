package de.dofe.ev3.reifen;

import lombok.Data;

@Data
public class Reifen {
    double durchmesser;
    double umfang;

    public Reifen(double durchmesser) {
        this.durchmesser = durchmesser;
        this.umfang = 2 * Math.PI * (this.durchmesser / 2);
    }
}
