package net.ihe.gazelle.common.filter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class ConverterDelegator implements Converter {

	private Converter converter;

	public ConverterDelegator(Converter converter) {
		super();
		this.converter = converter;
	}

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if (NullValue.NULL_VALUE_STRING.equals(arg2)) {
			return NullValue.NULL_VALUE;
		}
		return converter.getAsObject(arg0, arg1, arg2);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if (NullValue.NULL_VALUE.equals(arg2)) {
			return NullValue.NULL_VALUE_STRING;
		}
		return converter.getAsString(arg0, arg1, arg2);
	}

}
