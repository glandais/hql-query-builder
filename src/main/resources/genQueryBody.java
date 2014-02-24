
	public CLASSNAME(HQLQueryBuilder<?> queryBuilder) {
		super("this", queryBuilder);
	}
	
	public CLASSNAME(EntityManager entityManager) {
		super("this", new HQLQueryBuilder(entityManager,TYPE.class));
	}

