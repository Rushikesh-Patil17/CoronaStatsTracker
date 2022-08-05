package com.example.coronastatstracker;

public class Cases implements Comparable<Cases> {
    private final long confirmed;
    private final long recovered;
    private final long deceased;
    private final String state;

    public Cases(String state, long confirmed, long recovered, long deceased) {
        this.state = state;
        this.confirmed = confirmed;
        this.recovered = recovered;
        this.deceased = deceased;
    }

    public long getConfirmed() {
        return confirmed;
    }

    public long getRecovered() {
        return recovered;
    }

    public long getDeceased() {
        return deceased;
    }

    public String getState() {
        return state;
    }

    @Override
    public int compareTo(Cases o) {
        return (int) o.getConfirmed() - (int) this.getConfirmed();
    }
}
