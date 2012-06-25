package net.ihe.gazelle.hql.exchange.common;

import org.apache.commons.lang.StringUtils;

public class StreamFormatter implements DataFormatter<String, String> {

	private String indent;
	private int indentSize;

	public StreamFormatter() {
		super();
	}

	@Override
	public void readStart(String source) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object readValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readStartSubValue(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readEndSubValue() {
		// TODO Auto-generated method stub

	}

	@Override
	public void readEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeStart() {
		indentSize = 0;
		setIndent();
	}

	private void setIndent() {
		indent = StringUtils.repeat(" ", indentSize);
	}

	@Override
	public void writeValue(String key, Object value) {
		write(key + " = " + value);
	}

	private void write(String string) {
		System.out.println(indent + string);
	}

	@Override
	public void writeStartSubValue(String key) {
		indentSize++;
		setIndent();
		write(key);
	}

	@Override
	public void writeEndSubValue() {
		indentSize--;
		setIndent();
	}

	@Override
	public String writeEnd() {
		return "";
	}

}
