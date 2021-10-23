package io.github.glandais.hql.beans;

public class HQLStatistic<T> {

    private final T item;

    private final Integer[] counts;

    private final Integer id;

    public HQLStatistic(Integer id, T item, int n) {
        super();
        this.id = id;
        this.item = item;
        this.counts = new Integer[n];
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }
    }

    public Integer getId() {
        return id;
    }

    public T getItem() {
        return item;
    }

    public Integer[] getCounts() {
        return counts;
    }

}
