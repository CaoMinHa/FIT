package foxconn.fit.util;

import org.apache.commons.lang.StringUtils;

public class StringUtil {
	
	/**
	 * 
	 * @param likeStr
	 * @return
	 */
    public static String escapeSQLLike(String likeStr) {
        String str = StringUtils.replace(likeStr, "_", "/_");
        str = StringUtils.replace(str, "%","/%");
        str = StringUtils.replace(str, "?", "_");
        str = StringUtils.replace(str, "*", "%");
        return str;
    }
	
}
