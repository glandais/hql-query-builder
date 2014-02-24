
	public Class<?> getEntityClass() {
		return TYPE.class;
	}

	public void isNotEmpty() {
		queryBuilder.addRestriction(isNotEmptyRestriction());
	}

	public void isEmpty() {
		queryBuilder.addRestriction(isEmptyRestriction());
	}

	public HQLRestriction isNotEmptyRestriction() {
		return HQLRestrictions.isNotEmpty(path);
	}

	public HQLRestriction isEmptyRestriction() {
		return HQLRestrictions.isEmpty(path);
	}
