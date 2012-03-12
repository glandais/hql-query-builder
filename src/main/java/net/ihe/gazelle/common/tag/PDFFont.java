package net.ihe.gazelle.common.tag;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.pdf.ITextUtils;
import org.jboss.seam.pdf.ui.UIFont;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.xml.simpleparser.EntitiesToUnicode;

public class PDFFont extends UIFont {

	public static final String COMPONENT_TYPE = "net.ihe.gazelle.common.tag.PDFFont";

	Font font;

	int size = Font.UNDEFINED;
	String style;
	String color;
	boolean embedded = false;

	public int getSize() {
		return (Integer) valueBinding("size", size);
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyle() {
		return (String) valueBinding("style", style);
	}

	public String getColor() {
		return (String) valueBinding("color", color);
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean getEmbedded() {
		return (Boolean) valueBinding("embedded", embedded);
	}

	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public Object getITextObject() {
		return null; // we don't add to this component, so skip
	}

	@Override
	public void removeITextObject() {
		// font = null;
	}

	@Override
	public void createITextObject(FacesContext context) {
		boolean isJapanese = false;
		for (UIComponent child : this.getChildren()) {
			if (!isJapanese) {
				// ugly hack to be able to capture facelets text
				if (child.getFamily().equals("facelets.LiteralText")) {
					try {
						String text = EntitiesToUnicode.decodeString(extractText(context, child));
						if (containsJapanese(text)) {
							isJapanese = true;
						}
					} catch (IOException e) {
						// failed to get content
						e.printStackTrace();
					}
				}
			}
		}

		if (isJapanese) {
			font = FontFactory.getFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", getEmbedded(), getSize());
		} else {
			font = FontFactory.getFont(null, getSize());
		}
		if (getStyle() != null) {
			font.setStyle(getStyle());
		}
		if (getColor() != null) {
			font.setColor(ITextUtils.colorValue(getColor()));
		}
	}

	public boolean containsJapanese(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (isJapanese(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	// Read more: How to Detect a CJK Character in Java | eHow.com
	// http://www.ehow.com/how_11383680_detect-cjk-character-java.html#ixzz1kwduSuT8
	public boolean isJapanese(char c) {
		// simpler:
		return c > '\u00ff';
	}

	@Override
	public void handleAdd(Object o) {
		addToITextParent(o);
	}

}
