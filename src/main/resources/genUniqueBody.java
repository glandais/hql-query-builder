
	public CLASSNAME(EntityManager entityManager) {
		super("this", new HQLQueryBuilder(entityManager, TYPE.class));
	}

	public CLASSNAME(HQLQueryBuilder<?> queryBuilder) {
		super("this", queryBuilder);
	}

	public CLASSNAME(String path, HQLQueryBuilder<?> queryBuilder) {
		super(path, queryBuilder);
	}

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

	@SuppressWarnings("unchecked")
	public List<T> getListDistinct() {
		return (List<T>) queryBuilder.getListDistinct(path);
	}

	public int getCountOnPath() {
		return queryBuilder.getCountOnPath(path);
	}

	public int getCountDistinctOnPath() {
		return queryBuilder.getCountDistinctOnPath(path);
	}

	public void eq(T value) {
		queryBuilder.addRestriction(eqRestriction(value));
	}

	public void eqIfValueNotNull(T value) {
		if (value != null) {
			eq(value);
		}
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
