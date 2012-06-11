package net.ihe.gazelle.hql.beans;

public class HQLStatistic<T> {

	private T item;

	private Integer[] counts;

	private Integer id;

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
