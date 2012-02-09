package net.ihe.gazelle.common.filter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class StringConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return value;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return (String) value;
	}

}
