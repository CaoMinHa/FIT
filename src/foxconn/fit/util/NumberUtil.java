package foxconn.fit.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class NumberUtil {
	
	public static void FixedToTwoBit(Object obj,String[] fieldNames){
		try {
			if (obj!=null && fieldNames!=null && fieldNames.length>0) {
				Class<? extends Object> clazz = obj.getClass();
				for (String fieldName : fieldNames) {
					Field field = clazz.getDeclaredField(fieldName);
					field.setAccessible(true);
					Object value = field.get(obj);
					if (value instanceof String) {
						String str=(String) value;
						if (StringUtils.isNotEmpty(str)) {
							String newValue=BigDecimal.valueOf(Double.parseDouble(str)).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
							field.set(obj, newValue);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}
	
}
