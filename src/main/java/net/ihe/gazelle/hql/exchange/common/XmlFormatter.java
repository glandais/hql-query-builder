package net.ihe.gazelle.hql.exchange.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Deque;
import java.util.LinkedList;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class XmlFormatter implements DataFormatter {

	protected Deque<String> tags;
	protected BufferedWriter writer;

	public XmlFormatter() {
		super();
	}

	@Override
	public void readStart(InputStream source) throws DataException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object readValue(String key) throws DataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readStartSubValue(String key) throws DataException {
		// TODO Auto-generated method stub

	}

	@Override
	public void readEndSubValue() throws DataException {
		// TODO Auto-generated method stub

	}

	@Override
	public void readEnd() throws DataException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeStart(OutputStream outputStream) throws DataException {
		tags = new LinkedList<String>();
		writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		writeStartSubValue("export");
	}

	@Override
	public void writeValue(String key, Object value) throws DataException {
		try {
			writer.append(StringUtils.repeat(" ", tags.size() + 1));
			writer.append("<");
			writer.append(key);
			writer.append(">");
			if (value == null) {
				writer.append("null");
			} else {
				writer.append(StringEscapeUtils.escapeXml(value.toString()));
			}
			writer.append("</");
			writer.append(key);
			writer.append(">");
			writer.newLine();
		} catch (IOException e) {
			throw new DataException(e);
		}
	}

	@Override
	public void writeStartSubValue(String key) throws DataException {
		tags.addLast(key);
		try {
			writer.append(StringUtils.repeat(" ", tags.size()));
			writer.append("<");
			writer.append(key);
			writer.append(">");
			writer.newLine();
		} catch (IOException e) {
			throw new DataException(e);
		}
	}

	@Override
	public void writeEndSubValue() throws DataException {
		try {
			writer.append(StringUtils.repeat(" ", tags.size()));
			writer.append("</");
			writer.append(tags.removeLast());
			writer.append(">");
			writer.newLine();
		} catch (IOException e) {
			throw new DataException(e);
		}
	}

	@Override
	public void writeEnd() throws DataException {
		writeEndSubValue();
		try {
			writer.close();
		} catch (IOException e) {
			throw new DataException(e);
		}
	}

}
