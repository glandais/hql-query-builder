
	public CLASSNAME() {
		super("this", new HQLQueryBuilder(TYPE.class));
	}

	public CLASSNAME(HQLQueryBuilder<?> queryBuilder) {
		super("this", queryBuilder);
	}

