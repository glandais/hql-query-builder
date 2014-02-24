
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCount() {
		return queryBuilder.getCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> getList() {
		return (List<T>) queryBuilder.getList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> getListNullIfEmpty() {
		return (List<T>) queryBuilder.getListNullIfEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getUniqueResult() {
		return (T) queryBuilder.getUniqueResult();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<HQLStatistic<T>> getListWithStatistics(List<HQLStatisticItem> items) {
		List<?> result = queryBuilder.getListWithStatistics(items);
		return (List<HQLStatistic<T>>) result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Object> getListWithStatisticsItems(List<HQLStatisticItem> items, HQLStatistic<T> item,
			int statisticItemIndex) {
		HQLStatistic safeItem = item;
		List<Object> result = queryBuilder.getListWithStatisticsItems(items, safeItem, statisticItemIndex);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFirstResult(int firstResult) {
		queryBuilder.setFirstResult(firstResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaxResults(int maxResults) {
		queryBuilder.setMaxResults(maxResults);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCachable() {
		return queryBuilder.isCachable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCachable(boolean cachable) {
		queryBuilder.setCachable(cachable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addRestriction(HQLRestriction restriction) {
		queryBuilder.addRestriction(restriction);
	}