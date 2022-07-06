package foxconn.fit.dao.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * 与具体ORM实现无关的属性过滤条件封装类, 主要记录页面中简单的搜索过滤条件.
 * 
 */
public class PropertyFilter implements java.io.Serializable {

	private static final long serialVersionUID = -6068793306644318724L;
	
	/** 多个属性间OR关系的分隔符. */
	public static final String OR_SEPARATOR = "_OR_";
	
	/** 多个属性值的分隔符. */
	public static final String COMMA_SEPARATOR = ",";

	/**
	 * 属性比较类型
	 * EQ：表示等于
	 * NULL：表示为null
	 * NNULL：表示不为null
	 * LIKE：为模糊查询，匹配任何位置，相当于 %xx%
	 * LLIKE：为模糊查询，匹配以关键字开始的，相当于 xx%
	 * RLIKE：为模糊查询，匹配以关键字结束的的，相当于 %xx
	 * LT：表示小于
	 * GT：表示大于
	 * LE：表示小于等于
	 * GE：表示大于等于
	 * NE：表示不等于
	 * OREQ：表示或者等于几个值
	 * ORLIKE：表示或者相似几个值
	 * NOREQ:处理not in 的条件结果集
	 * MAKE : 自定义替换内容   完整替换
	 */
	public enum MatchType {
		EQ, NULL, NNULL, LIKE, LLIKE, RLIKE, LT, GT, LE, GE, NE, OREQ, ORLIKE,NOREQ,MAKE;
	}
	


	/** 属性数据类型. */
	public enum PropertyType {
		S(String.class), I(Integer.class), L(Long.class), N(Double.class), D(
				Date.class), B(Boolean.class);

		private Class<?> clazz;

		private PropertyType(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Class<?> getValue() {
			return clazz;
		}
	}
	
	/**
	 * 根据匹配类型以及属性类型，对属性的匹配方式归类
	 * @return
	 */
	public static String matchTypeByPropertyType(){
		String item = "{\"S\":[" +
				              "{\"enName\":\"EQ\",\"cnName\":\"等于\"}," +
				              "{\"enName\":\"LIKE\",\"cnName\":\"包含\"}," +
				              "{\"enName\":\"LLIKE\",\"cnName\":\"起始包含\"}," +
				              "{\"enName\":\"RLIKE\",\"cnName\":\"末尾包含\"}," +
				              "{\"enName\":\"LT\",\"cnName\":\"小于\"}," +
				              "{\"enName\":\"LE\",\"cnName\":\"小于等于\"}," +
				              "{\"enName\":\"GT\",\"cnName\":\"大于\"}," +
				              "{\"enName\":\"GE\",\"cnName\":\"大于等于\"}]," +
				       "\"I\":[" +
				              "{\"enName\":\"EQ\",\"cnName\":\"等于\"}," +
				              "{\"enName\":\"LT\",\"cnName\":\"小于\"}," +
				              "{\"enName\":\"LE\",\"cnName\":\"小于等于\"}," +
				              "{\"enName\":\"GT\",\"cnName\":\"大于\"}," +
				              "{\"enName\":\"GE\",\"cnName\":\"大于等于\"}]," +
				       "\"N\":[" +
				              "{\"enName\":\"EQ\",\"cnName\":\"等于\"}," +
			                  "{\"enName\":\"LT\",\"cnName\":\"小于\"}," +
			                  "{\"enName\":\"LE\",\"cnName\":\"小于等于\"}," +
			                  "{\"enName\":\"GT\",\"cnName\":\"大于\"}," +
			                  "{\"enName\":\"GE\",\"cnName\":\"大于等于\"}]," +
				       "\"D\":[" +
			                  "{\"enName\":\"EQ\",\"cnName\":\"等于\"}," +
		                      "{\"enName\":\"LT\",\"cnName\":\"小于\"}," +
		                      "{\"enName\":\"LE\",\"cnName\":\"小于等于\"}," +
		                      "{\"enName\":\"GT\",\"cnName\":\"大于\"}," +
		                      "{\"enName\":\"GE\",\"cnName\":\"大于等于\"}]" +
				     "}";
		return item;
	}
	
	private MatchType matchType = null;
	private Object matchValue = null;

	private Class<?> propertyClass = null;
	private String[] propertyNames = null;

	public PropertyFilter() {
	}

	/**
	 * @param filterName
	 *            比较属性字符串,含待比较的比较类型、属性值类型及属性列表. eg. LIKES_NAME_OR_LOGIN_NAME
	 * @param value
	 *            待比较的值.
	 */
	public PropertyFilter(final String filterName, final String value) {

		String firstPart = StringUtils.substringBefore(filterName, "_");
		String matchTypeCode = StringUtils.substring(filterName, 0, firstPart.length() - 1);
		String propertyTypeCode = StringUtils.substring(filterName, firstPart.length() - 1, firstPart.length());

		try {
			matchType = Enum.valueOf(MatchType.class, matchTypeCode);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("filter名称" + filterName+ "没有按规则编写,无法得到属性比较类型.", e);
		}

		try {
			propertyClass = Enum.valueOf(PropertyType.class, propertyTypeCode).getValue();
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("filter名称" + filterName+ "没有按规则编写,无法得到属性值类型.", e);
		}

		String propertyNameStr = StringUtils.substringAfter(filterName, "_");
		
		Assert.hasText(propertyNameStr, "filter名称"+ filterName + "没有按规则编写,无法得到属性名称.");
		
		propertyNames = StringUtils.splitByWholeSeparator(propertyNameStr,PropertyFilter.OR_SEPARATOR);
		ConvertUtils.register(new DateLocaleConverter(Locale.CHINA,"yyyy-MM-dd HH:mm:ss"), Date.class);
//		if (StringUtils.isNotEmpty(value) && value.indexOf(",")>0) {
//			this.matchValue = ConvertUtils.convert(value.split(COMMA_SEPARATOR), propertyClass);
//		}else {
			this.matchValue = ConvertUtils.convert(value, propertyClass);
//		}
	}

	/**
	 * 从HttpRequest中创建PropertyFilter列表, 默认Filter属性名前缀为filter.
	 * 
	 * @see #buildFromHttpRequest(HttpServletRequest, String)
	 */
	public static List<PropertyFilter> buildFromHttpRequest(final HttpServletRequest request) {
		return buildFromHttpRequest(request, "filter");
	}

	/**
	 * 从HttpRequest中创建PropertyFilter列表
	 * PropertyFilter命名规则为Filter属性前缀_比较类型属性类型_属性名.
	 * 
	 * eg. filter_EQS_name filter_LIKES_name_OR_email
	 */
	public static List<PropertyFilter> buildFromHttpRequest(
			final HttpServletRequest request, final String filterPrefix) {
		List<PropertyFilter> filterList = new ArrayList<PropertyFilter>();

		// 从request中获取含属性前缀名的参数,构造去除前缀名后的参数Map.
		Map<String, Object> filterParamMap = getParametersStartingWith(request, filterPrefix + "_");

		// 分析参数Map,构造PropertyFilter列表
		for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
			String filterName = entry.getKey();
			Object v = entry.getValue();
			String[] values = new String[]{};
			if( v instanceof String[]){
				values = (String[]) v;
			}else{
				values = new String[]{(String)v};
			}
			for(String val : values){
				// 如果value值为空,则忽略此filter.
				if (StringUtils.isNotBlank(val)) {
					PropertyFilter filter = new PropertyFilter(filterName, val);
					filterList.add(filter);
				}
			}
			
		}

		return filterList;
	}

	/**
	 * 获取比较值的类型.
	 */
	public Class<?> getPropertyClass() {
		return propertyClass;
	}

	/**
	 * 获取比较方式.
	 */
	public MatchType getMatchType() {
		return matchType;
	}

	/**
	 * 获取比较值.
	 */
	public Object getMatchValue() {
		return matchValue;
	}

	/**
	 * 获取比较属性名称列表.
	 */
	public String[] getPropertyNames() {
		return propertyNames;
	}

	/**
	 * 获取唯一的比较属性名称.
	 */
	public String getPropertyName() {
//		AssertUtils.isTrue(propertyNames.length == 1,
//				"There are not only one property in this filter.");
		if(propertyNames.length != 1){
			throw new IllegalArgumentException("There are not only one property in this filter.");
		}
		return propertyNames[0];
	}

	/**
	 * 是否比较多个属性.
	 */
	public boolean hasMultiProperties() {
		return (propertyNames.length > 1);
	}
	
	
	/**
	 * 取得带相同前缀的Request Parameters.
	 * 
	 * 返回的结果的Parameter名已去除前缀.
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
		if(null == request){
			throw new IllegalArgumentException("Request must not be null");
		}
		Enumeration paramNames = request.getParameterNames();
		Map<String, Object> params = new TreeMap<String, Object>();
		if (prefix == null) {
			prefix = "";
		}
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if ("".equals(prefix) || paramName.startsWith(prefix)) {
				String unprefixed = paramName.substring(prefix.length());
				String[] values = request.getParameterValues(paramName);
				if (values == null || values.length == 0) {
					// Do nothing, no values found at all.
				} else if (values.length > 1) {
					params.put(unprefixed, values);
				} else {
					params.put(unprefixed, values[0]);
				}
			}
		}
		return params;
	}
}
