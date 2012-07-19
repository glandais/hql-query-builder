
	public boolean isSingle() {
		return true;
	}

	public void eq(T value) {
		queryBuilder.addRestriction(eqRestriction(value));
	}

	public void neq(T value) {
		queryBuilder.addRestriction(neqRestriction(value));
	}

	public void in(Collection<? extends T> elements) {
		queryBuilder.addRestriction(inRestriction(elements));
	}

	public void nin(Collection<? extends T> elements) {
		queryBuilder.addRestriction(ninRestriction(elements));
	}

	public void isNotNull() {
		queryBuilder.addRestriction(isNotNullRestriction());
	}

	public void isNull() {
		queryBuilder.addRestriction(isNullRestriction());
	}

	public HQLRestriction eqRestriction(T value) {
		return HQLRestrictions.eq(path, value);
	}

	public HQLRestriction neqRestriction(T value) {
		return HQLRestrictions.neq(path, value);
	}

	public HQLRestriction inRestriction(Collection<? extends T> elements) {
		return HQLRestrictions.in(path, elements);
	}

	public HQLRestriction ninRestriction(Collection<? extends T> elements) {
		return HQLRestrictions.nin(path, elements);
	}

	public HQLRestriction isNotNullRestriction() {
		return HQLRestrictions.isNotNull(path);
	}

	public HQLRestriction isNullRestriction() {
		return HQLRestrictions.isNull(path);
	}
