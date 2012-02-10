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
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (NullValue.NULL_VALUE_STRING.equals(value)) {
			return NullValue.NULL_VALUE;
		}
		return converter.getAsObject(context, component, value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (NullValue.NULL_VALUE.equals(value)) {
			return NullValue.NULL_VALUE_STRING;
		}
		return converter.getAsString(context, component, value);
	}

}
