package net.ihe.gazelle.common.tag;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class ImageLinkComponent extends AbstractLinkComponent {

	public ImageLinkComponent() {
		super();
	}

	public String getFamily() {
		return "gazelle-imagelink";
	}

	public void outputLink(FacesContext context, ResponseWriter writer, Object value, boolean isDetailed,
			String contextPath) throws IOException {
		if (value != null) {
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

			writer.startElement("img", this);
			String icon = getAttributeValueString("icon");
			writer.writeAttribute("src", contextPath + "/" + icon, "icon");
			String width = getAttributeValueString("width");
			if (width != null) {
				writer.writeAttribute("width", width, "width");
			}
			String height = getAttributeValueString("height");
			if (height != null) {
				writer.writeAttribute("height", height, "height");
			}
			writer.endElement("img");

			writer.endElement("a");
		}
	}

}
