package net.ihe.gazelle.common.filter.action;

import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Init;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Name("genericConverter")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class GenericConverterBean implements GenericConverterLocal {

	private static Logger log = LoggerFactory.getLogger(GenericConverterBean.class);

	@Observer("org.jboss.seam.postInitialization")
	public void registerConverters() {
		log.info("Discovering entities to convert automatically");

		EntityManager entityManager = (EntityManager) Component.getInstance("entityManager");
		Object delegate = entityManager.getDelegate();

		SessionFactoryImplementor factory = null;

		if (delegate instanceof Session) {
			Session session = (Session) delegate;
			SessionFactory sessionFactory = session.getSessionFactory();
			if (sessionFactory instanceof SessionFactoryImplementor) {
				factory = (SessionFactoryImplementor) sessionFactory;
			}
		}
		if (factory == null) {
			throw new IllegalArgumentException();
		}

		@SuppressWarnings("unchecked")
		Map<String, ClassMetadata> allClassMetadata = factory.getAllClassMetadata();

		Collection<ClassMetadata> values = allClassMetadata.values();
		for (ClassMetadata classMetadata : values) {
			Class<?> mappedClass = classMetadata.getMappedClass(EntityMode.POJO);
			Init.instance().getConvertersByClass().put(mappedClass, "org.jboss.seam.ui.EntityConverter");
		}

	}

	@Create
	public void init() {
		// ?
	}

	@Destroy
	public void stopProxy() {
		// ?
	}
}
