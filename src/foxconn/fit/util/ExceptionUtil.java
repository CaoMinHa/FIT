package foxconn.fit.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ExceptionUtil extends ExceptionUtils {

	public static String getMessage(Throwable th) {
		if (th == null) {
			return "";
		} else {
			return StringUtils.defaultString(th.getMessage());
		}
	}

	public static String getRootCauseMessage(Throwable th) {
		Throwable root = getRootCause(th);
		root = root != null ? root : th;
		return getMessage(root);
	}

}
