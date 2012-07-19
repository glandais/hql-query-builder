
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
	public List<TYPE> getList() {
		return (List<TYPE>) queryBuilder.getList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TYPE> getListNullIfEmpty() {
		return (List<TYPE>) queryBuilder.getListNullIfEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TYPE getUniqueResult() {
		return (TYPE) queryBuilder.getUniqueResult();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<HQLStatistic<TYPE>> getListWithStatistics(List<HQLStatisticItem> items) {
		List<?> result = queryBuilder.getListWithStatistics(items);
		return (List<HQLStatistic<TYPE>>) result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Object> getListWithStatisticsItems(List<HQLStatisticItem> items, HQLStatistic<TYPE> item,
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