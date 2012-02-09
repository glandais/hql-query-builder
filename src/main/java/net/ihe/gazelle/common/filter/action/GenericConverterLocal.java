package net.ihe.gazelle.common.filter.action;

import javax.ejb.Local;

@Local
public interface GenericConverterLocal {

	void registerConverters();

	void init();

	void stopProxy();

}
