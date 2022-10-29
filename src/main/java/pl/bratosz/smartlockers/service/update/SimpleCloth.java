package pl.bratosz.smartlockers.service.update;

import pl.bratosz.smartlockers.model.SimpleEmployee;
import pl.bratosz.smartlockers.model.clothes.Cloth;

import java.time.LocalDate;
import java.util.Objects;

public class SimpleCloth implements Comparable<SimpleCloth>{
    private long barcode;
    private LocalDate assignment;
    private LocalDate lastWashing;
    private SimpleEmployee simpleEmployee;

    public SimpleCloth(long barcode, LocalDate assignment, LocalDate lastWashing) {
        this.barcode = barcode;
        this.assignment = assignment;
        this.lastWashing = lastWashing;
    }

    public SimpleCloth(Cloth c) {
        this.barcode = c.getBarcode();
        this.assignment = c.getAssignment();
        this.lastWashing = c.getLastWashing();
        this.simpleEmployee =  new SimpleEmployee(this, c.getEmployee());
    }

    public long getBarcode() {
        return barcode;
    }

    public LocalDate getLastWashing() {
        return lastWashing;
    }

    public SimpleEmployee getSimpleEmployee() {
        return simpleEmployee;
    }

    public LocalDate getAssignment() {
        return assignment;
    }

    public void setSimpleEmployee(SimpleEmployee simpleEmployee) {
        this.simpleEmployee = simpleEmployee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleCloth that = (SimpleCloth) o;
        return getBarcode() == that.getBarcode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBarcode());
    }

    @Override
    public int compareTo(SimpleCloth c) {
        return Long.valueOf(this.getBarcode()).compareTo(c.getBarcode());
    }
}
