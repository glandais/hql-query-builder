package net.ihe.gazelle.common.filter.action;

import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

@Name("autoLoginBean")
@Scope(ScopeType.PAGE)
public class AutoLoginBean implements Serializable {

	private static final long serialVersionUID = 8590697300241014807L;

	// Ten years
	private static final int MAX_AGE = 60 * 60 * 24 * 365 * 10;

	public boolean isAutologin() {
		String requestURL = getRequestURL();
		if (requestURL.contains("identity.logout")) {
			setUserLoggedOut(true);
			return false;
		}

		if (!Identity.instance().isLoggedIn()) {
			return !getUserLoggedOut();
		} else {
			setUserLoggedOut(false);
			return false;
		}
	}

	public String getMonitoringPage() {
		return getURLBase() + "autoLoginMonitoring.seam";
	}

	public String getCASPage() {
		return getURLBase() + "cas/home.seam";
	}

	private String getURLBase() {
		String queryString = getRequestURL();
		int ordinalIndexOf = StringUtils.ordinalIndexOf(queryString, "/", 4);
		String url = queryString.substring(0, ordinalIndexOf + 1);
		return url;
	}

	private String getRequestURL() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest catalinaRequest = (HttpServletRequest) externalContext.getRequest();
		String reqUrl = catalinaRequest.getRequestURL().toString();
		String queryString = catalinaRequest.getQueryString();
		if (queryString != null) {
			reqUrl += "?" + queryString;
		}
		return reqUrl;
	}

	private boolean getUserLoggedOut() {
		Boolean result = false;
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("UserLoggedOut")) {
				String value = cookie.getValue();
				result = Boolean.parseBoolean(value);
			}
		}
		return result;
	}

	private void setUserLoggedOut(boolean value) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		Cookie cookie = new Cookie("UserLoggedOut", Boolean.toString(value));

		String queryString = getRequestURL();
		int ordinalIndexOf3 = StringUtils.ordinalIndexOf(queryString, "/", 3);
		int ordinalIndexOf4 = StringUtils.ordinalIndexOf(queryString, "/", 4);
		String url = queryString.substring(ordinalIndexOf3, ordinalIndexOf4 + 1);

		cookie.setPath(url);
		cookie.setMaxAge(MAX_AGE);
		response.addCookie(cookie);
	}

}
