package net.ihe.gazelle.hql.exchange.common;

public interface DataFormatter<R, W> {

	void readStart(R source);

	Object readValue(String key);

	void readStartSubValue(String key);

	void readEndSubValue();

	void readEnd();

	void writeStart();

	void writeValue(String key, Object value);

	void writeStartSubValue(String key);

	void writeEndSubValue();

	W writeEnd();

}
