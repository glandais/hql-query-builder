package net.ihe.gazelle.common.tag;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class LinkComponent extends AbstractLinkComponent {

	public LinkComponent() {
		super();
	}

	public String getFamily() {
		return "gazelle-link";
	}

	public void outputLink(FacesContext context, ResponseWriter writer, Object value, boolean isDetailed,
			String contextPath) throws IOException {
		LinkDataProvider provider = getProvider(value);

		writer.startElement("a", this);
		String url = getURL(provider, value, contextPath);
		writer.writeURIAttribute("href", context.getExternalContext().encodeResourceURL(url), "href");

		String target = getAttributeValueString("target");
		if (target != null && target.trim().length() != 0) {
			writer.writeAttribute("target", target, "target");
		}
		String styleClass = getAttributeValueString("styleClass");
		if (styleClass != null) {
			writer.writeAttribute("class", styleClass, "styleClass");
		}

		writer.write(getText(provider, value, isDetailed));
		writer.endElement("a");
	}

}
