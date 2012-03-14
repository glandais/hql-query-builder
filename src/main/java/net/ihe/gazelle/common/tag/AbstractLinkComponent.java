package net.ihe.gazelle.common.tag;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletContext;

public abstract class AbstractLinkComponent extends UIComponentBase {

	protected static ServiceLoader<LinkDataProvider> PROVIDERS = ServiceLoader.load(LinkDataProvider.class);

	public static LinkDataProvider getProviderForClass(Class<?> valueClass) {
		for (LinkDataProvider dataProvider : PROVIDERS) {
			List<Class<?>> supportedClasses = dataProvider.getSupportedClasses();
			for (Class<?> supportedClass : supportedClasses) {
				if (supportedClass.isAssignableFrom(valueClass)) {
					return dataProvider;
				}
			}
		}
		return null;
	}

	protected boolean getAttributeValueBoolean(String attributeName) {
		Object detailed = getAttributes().get(attributeName);
		boolean result = false;
		if (detailed != null) {
			if (detailed instanceof Boolean) {
				result = (Boolean) detailed;
			} else if (detailed instanceof String) {
				result = Boolean.valueOf((String) detailed);
			}
		}
		return result;
	}

	protected String getAttributeValueString(String attributeName) {
		Object detailed = getAttributes().get(attributeName);
		String result = null;
		if (detailed != null) {
			result = detailed.toString();
		}
		return result;
	}

	protected LinkDataProvider getProvider(Object value) {
		if (value != null) {
			Class<?> valueClass = value.getClass();
			return getProviderForClass(valueClass);
		}
		return null;
	}

	public String getURL(LinkDataProvider provider, Object value, String contextPath) {
		if (provider == null) {
			return "";
		} else {
			if (value == null) {
				return "";
			} else {
				return contextPath + "/" + provider.getLink(value);
			}
		}
	}

	public String getText(LinkDataProvider provider, Object value, boolean isDetailed) {
		if (provider == null) {
			return "";
		} else {
			if (value == null) {
				return "";
			} else {
				return provider.getLabel(value, isDetailed);
			}
		}
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		ExternalContext externalContext = context.getExternalContext();
		ServletContext servletContext = (ServletContext) externalContext.getContext();
		String contextPath = servletContext.getContextPath();

		ResponseWriter writer = context.getResponseWriter();

		Object value = getAttributes().get("value");
		boolean isDetailed = getAttributeValueBoolean("detailed");

		if (value instanceof Iterable) {
			Iterable<?> iterable = (Iterable<?>) value;
			Iterator<?> iterator = iterable.iterator();
			while (iterator.hasNext()) {
				Object element = iterator.next();
				if (element != null) {
					outputLink(context, writer, element, isDetailed, contextPath);
					if (iterator.hasNext()) {
						writer.write(" / ");
					}
				}
			}
		} else {
			if (value != null) {
				outputLink(context, writer, value, isDetailed, contextPath);
			}
		}
		writer.flush();
	}

	public abstract void outputLink(FacesContext context, ResponseWriter writer, Object element, boolean isDetailed,
			String contextPath) throws IOException;

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		if (isRendered()) {
			return;
		}
	}

}
