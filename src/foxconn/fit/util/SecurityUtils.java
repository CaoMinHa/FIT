package foxconn.fit.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import foxconn.fit.service.base.UserDetailImpl;

public class SecurityUtils {

	public static String getLoginUsername() {

		UserDetailImpl user = getLoginUser();
		if (user != null) {
			return user.getUsername();
		} else {
			return "";
		}

	}
	
	public static String getCorporationCode() {
		UserDetailImpl user = getLoginUser();
		if (user != null) {
			return user.getCorporationCode();
		} else {
			return "";
		}
	}
	
	public static String getEntity() {
		UserDetailImpl user = getLoginUser();
		if (user != null) {
			return user.getEntity();
		} else {
			return "";
		}
	}
	
	public static String getEBS() {
		UserDetailImpl user = getLoginUser();
		if (user != null) {
			return user.getEBS();
		} else {
			return "";
		}
	}
	
	public static String[] getMenus() {
		UserDetailImpl user = getLoginUser();
		if (user != null) {
			String menu = user.getMenus();
			if (StringUtils.isNotEmpty(menu)) {
				return menu.split(",");
			}
		}
		
		return null;
	}

	public static String[] getPoCenter(){
		UserDetailImpl user = getLoginUser();
		if (user != null) {
			String poCenter = user.getPoCenter();
			if (StringUtils.isNotEmpty(poCenter)) {
				return poCenter.split(",");
			}
		}
		return null;
	}
	
	public static UserDetailImpl getLoginUser() {
		SecurityContext context = SecurityContextHolder.getContext();

		if (context != null) {
			Authentication auth = context.getAuthentication();

			if (auth != null) {
				Object principal = auth.getPrincipal();

				if (principal instanceof UserDetailImpl) {
					return (UserDetailImpl) principal;
				} else {
					return null;
				}

			}
		}

		return null;
	}

}
