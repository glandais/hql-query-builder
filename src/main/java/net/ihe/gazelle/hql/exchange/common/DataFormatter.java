package net.ihe.gazelle.hql.exchange.common;

import java.io.InputStream;
import java.io.OutputStream;

public interface DataFormatter {

	void readStart(InputStream source) throws DataException;

	Object readValue(String key) throws DataException;

	void readStartSubValue(String key) throws DataException;

	void readEndSubValue() throws DataException;

	void readEnd() throws DataException;

	void writeStart(OutputStream outputStream) throws DataException;

	void writeValue(String key, Object value) throws DataException;

	void writeStartSubValue(String key) throws DataException;

	void writeEndSubValue() throws DataException;

	void writeEnd() throws DataException;

}
