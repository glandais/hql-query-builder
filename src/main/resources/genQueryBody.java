
	public CLASSNAME() {
		super("this", new HQLQueryBuilder(TYPE.class));
	}

	public CLASSNAME(EntityManager em) {
		super("this", new HQLQueryBuilder(em, TYPE.class));
	}

	public CLASSNAME(HQLQueryBuilder<?> queryBuilder) {
		super("this", queryBuilder);
	}

