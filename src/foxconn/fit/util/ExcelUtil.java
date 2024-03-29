package foxconn.fit.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class ExcelUtil {

	public static int PAGE_SIZE=10000;
	
	public static String getCellStringValue(Cell cell,int sheetIndex,int rowIndex) throws Exception {
		try {
			return getCellStringValue(cell);
		} catch (Exception e) {
			throw new RuntimeException("第"+(sheetIndex+1)+"个sheet第"+(rowIndex+1)+"行第"+cell.getColumnIndex()+"列数据格式错误 : "+ExceptionUtil.getRootCauseMessage(e));
		}
	}
	
	public static String getCellStringValue(Cell cell,int rowIndex) throws Exception {
		try {
			return getCellStringValue(cell);
		} catch (Exception e) {
			throw new RuntimeException("第"+(rowIndex+1)+"行"+cell.getColumnIndex()+"列数据格式错误 : "+ExceptionUtil.getRootCauseMessage(e));
		}
	}
	
	private static String getCellStringValue(Cell cell) throws Exception {
		String cellValue = "";

		if (cell!=null) {
			switch (cell.getCellType()) {
			case NUMERIC: // 数字
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					cellValue = DateUtil.formatByddSMMSyyyy(date);
				}else{
					double d=cell.getNumericCellValue();
					Double dou=Double.valueOf(d);
					cellValue = dou.toString();
					if(dou<2147483647){
						int indexOf = cellValue.indexOf(".");
						if (cellValue.indexOf("E")>0 || (indexOf>0 && cellValue.substring(indexOf+1).length()>6)) {
							cellValue=new DecimalFormat("0.000000").format(d);
							while (cellValue.endsWith("0")) {
								cellValue=cellValue.substring(0, cellValue.length()-1);
							}
							if (cellValue.endsWith(".")) {
								cellValue=cellValue.substring(0, cellValue.length()-1);
							}
						}
						
						int i = dou.intValue();
						if (i==d) {
							cellValue=String.valueOf(i);
						}
					}else{
						//非数字，四舍五入到0，当做字符串处理
						cellValue=new DecimalFormat("0").format(d);
					}
				}
				
				break;
			case STRING: // 字符串
				cellValue = cell.getStringCellValue();
				break;
			case BOOLEAN: // Boolean
				cellValue = cell.getBooleanCellValue() + "";
				break;
			case FORMULA: // 公式
				try {
					cellValue = cell.getStringCellValue();
				} catch (Exception e) {
					double d=cell.getNumericCellValue();
					Double dou=Double.valueOf(d);
					cellValue = dou.toString();
					if(dou<2147483647){
						int indexOf = cellValue.indexOf(".");
						if (cellValue.indexOf("E")>0 || (indexOf>0 && cellValue.substring(indexOf+1).length()>6)) {
							cellValue=new DecimalFormat("0.000000").format(d);
							while (cellValue.endsWith("0")) {
								cellValue=cellValue.substring(0, cellValue.length()-1);
							}
							if (cellValue.endsWith(".")) {
								cellValue=cellValue.substring(0, cellValue.length()-1);
							}
						}
						
						int i = dou.intValue();
						if (i==d) {
							cellValue=String.valueOf(i);
						}
					}else{
						//非数字，四舍五入到0，当做字符串处理
						cellValue=new DecimalFormat("0").format(d);
					}
				}
				break;
			case BLANK: // 空值
				cellValue = "";
				break;
			case ERROR: // 故障
				throw new Exception("无效字符(Invalid Characters)-->"+cell);
			default:
				throw new Exception("未知类型(Unknown Type)-->"+cell);
			}
		}

		return cellValue.trim();
	}
	
	public static String getCellStringValueNoRounding(Cell cell,int sheetIndex,int rowIndex) throws Exception {
		try {
			return getCellStringValueNoRounding(cell);
		} catch (Exception e) {
			throw new RuntimeException("第"+(sheetIndex+1)+"个sheet第"+(rowIndex+1)+"行第"+cell.getColumnIndex()+"列数据格式错误 : "+ExceptionUtil.getRootCauseMessage(e));
		}
	}
	
	public static String getCellStringValueNoRounding(Cell cell,int rowIndex) throws Exception {
		try {
			return getCellStringValueNoRounding(cell);
		} catch (Exception e) {
			throw new RuntimeException("第"+(rowIndex+1)+"行"+cell.getColumnIndex()+"列数据格式错误 : "+ExceptionUtil.getRootCauseMessage(e));
		}
	}
	
	private static String getCellStringValueNoRounding(Cell cell) throws Exception {
		String cellValue = "";

		if (cell!=null) {
			switch (cell.getCellType()) {
			case NUMERIC: // 数字
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					cellValue = DateUtil.formatByddSMMSyyyy(date);
				}else{
					double d=cell.getNumericCellValue();
					Double dou=Double.valueOf(d);
					cellValue = dou.toString();
					if(dou<2147483647){
						int i = dou.intValue();
						if (i==d) {
							cellValue=String.valueOf(i);
						}
					}else{
						//非数字，四舍五入到0，当做字符串处理
						cellValue=new DecimalFormat("0").format(d);
					}
				}
				
				break;
			case STRING: // 字符串
				cellValue = cell.getStringCellValue();
				break;
			case BOOLEAN: // Boolean
				cellValue = cell.getBooleanCellValue() + "";
				break;
			case FORMULA: // 公式
				try {
					cellValue = cell.getStringCellValue();
				} catch (Exception e) {
					double d=cell.getNumericCellValue();
					Double dou=Double.valueOf(d);
					cellValue = dou.toString();
					if(dou<2147483647){
						int i = dou.intValue();
						if (i==d) {
							cellValue=String.valueOf(i);
						}
					}else{
						//非数字，四舍五入到0，当做字符串处理
						cellValue=new DecimalFormat("0").format(d);
					}
				}
				break;
			case BLANK: // 空值
				cellValue = "";
				break;
			case ERROR: // 故障
				throw new Exception("无效字符(Invalid Characters)-->"+cell);
			default:
				throw new Exception("未知类型(Unknown Type)-->"+cell);
			}
		}

		return cellValue.trim();
	}
	
	public static int getCellIntegerValue(Cell cell,int rowIndex) throws Exception {
		int cellValue = 0;

		try {
			if (cell!=null&&cell.toString()!="") {
				switch (cell.getCellType()) {
				case BLANK: // 空值
					cellValue = 0;
					break;
				case NUMERIC: // 数字
					cellValue=Double.valueOf(cell.getNumericCellValue()).intValue();
					break;
				case STRING: // 字符串
					cellValue = Integer.parseInt(cell.getStringCellValue());
					break;
				case FORMULA: // 公式
					cellValue=Double.valueOf(cell.getNumericCellValue()).intValue();
					break;
				case ERROR: // 故障
					throw new Exception("无效字符(Invalid Characters)-->"+cell);
				default:
					throw new Exception("未知类型(Unknown Type)-->"+cell);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("第"+(rowIndex+1)+"行"+cell.getColumnIndex()+"列数据格式错误 : "+ExceptionUtil.getRootCauseMessage(e));
		}

		return cellValue;
	}
	
	public static double getCellDoubleValue(Cell cell,int rowIndex) throws Exception {
		double cellValue = 0;

		try {
			if (cell!=null&&cell.toString()!="") {
				switch (cell.getCellType()) {
				case BLANK: // 空值
					cellValue = 0;
					break;
				case NUMERIC: // 数字
					cellValue=cell.getNumericCellValue();
					break;
				case STRING: // 字符串
					cellValue = Double.parseDouble(cell.getStringCellValue());
					break;
				case FORMULA: // 公式
					cellValue=cell.getNumericCellValue();
					break;
				case ERROR: // 故障
					throw new Exception("无效字符(Invalid Characters)-->"+cell);
				default:
					throw new Exception("未知类型(Unknown Type)-->"+cell);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("第"+(rowIndex+1)+"行"+cell.getColumnIndex()+"列数据格式错误 : "+ExceptionUtil.getRootCauseMessage(e));
		}

		return cellValue;
	}
	
	public static Date getCellDateValue(Cell cell,SimpleDateFormat format) throws Exception {
		Date cellValue = null;

		if (cell!=null) {
			switch (cell.getCellType()) {
			case NUMERIC: // 数字
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					cellValue = cell.getDateCellValue();
				}
				
				break;
			case STRING: // 字符串
				cellValue = format.parse(cell.getStringCellValue());
				break;
			case FORMULA: // 公式
				cellValue = format.parse(cell.getStringCellValue());
				break;
			case ERROR: // 故障
				throw new Exception("无效字符(Invalid Characters)-->"+cell);
			default:
				return null;
			}
		}

		return cellValue;
	}
	public static void addTitleP(Row row,String year){
		int yearNumber=Integer.parseInt(year.substring(2));
		String yearFY="FY"+year.substring(2);
		String nextFY="FY"+(yearNumber+1);
		String nextTwoFY="FY"+(yearNumber+2);
		String nextThreeFY="FY"+(yearNumber+3);
		String nextFourFY="FY"+(yearNumber+4);
		row.createCell(2).setCellValue(yearFY);
		row.createCell(182).setCellValue(nextFY);
		row.createCell(197).setCellValue(nextTwoFY);
		row.createCell(212).setCellValue(nextThreeFY);
		row.createCell(227).setCellValue(nextFourFY);
	}
	public static void addTitle(Row row,String year){
		int yearNumber=Integer.parseInt(year.substring(2));
		String preFY="FY"+(yearNumber-2);
		String lastFY="FY"+(yearNumber-1);
		String yearFY="FY"+year.substring(2);
		String nextFY="FY"+(yearNumber+1);
		String nextTwoFY="FY"+(yearNumber+2);
		String nextThreeFY="FY"+(yearNumber+3);
		String nextFourFY="FY"+(yearNumber+4);

		for (int i=1;i<10;i++){
			row.createCell(7*i+2).setCellValue(preFY);
			row.createCell(7*i+3).setCellValue(lastFY);
			row.createCell(7*i+4).setCellValue(yearFY);
			row.createCell(7*i+5).setCellValue(nextFY);
			row.createCell(7*i+6).setCellValue(nextTwoFY);
			row.createCell(7*i+7).setCellValue(nextThreeFY);
			row.createCell(7*i+8).setCellValue(nextFourFY);
		}
		row.createCell(72).setCellValue(preFY);
		row.createCell(85).setCellValue(lastFY);
		row.createCell(98).setCellValue(yearFY);
		row.createCell(111).setCellValue(nextFY);
		row.createCell(112).setCellValue(nextTwoFY);
		row.createCell(113).setCellValue(nextThreeFY);
		row.createCell(114).setCellValue(nextFourFY);

		row.createCell(115).setCellValue(preFY);
		row.createCell(127).setCellValue(lastFY);
		row.createCell(139).setCellValue(yearFY);
		row.createCell(151).setCellValue(nextFY);
		row.createCell(152).setCellValue(nextTwoFY);
		row.createCell(153).setCellValue(nextThreeFY);
		row.createCell(154).setCellValue(nextFourFY);

		row.createCell(155).setCellValue(preFY);
		row.createCell(168).setCellValue(lastFY);
		row.createCell(181).setCellValue(yearFY);
		row.createCell(195).setCellValue(nextFY);
		row.createCell(196).setCellValue(nextTwoFY);
		row.createCell(197).setCellValue(nextThreeFY);
		row.createCell(198).setCellValue(nextFourFY);
	}
	
	public static void createForecastDetailRevenueCell(Map map,Row row) throws Exception{
		Cell cell  = row.createCell(0);
		cell.setCellValue(mapValString(map.get("ENTITY")));
		row.createCell(1).setCellValue(mapValString(map.get("MAKE_ENTITY")));
		row.createCell(2).setCellValue(mapValString(map.get("INDUSTRY")));
		row.createCell(3).setCellValue(mapValString(map.get("PRODUCT")));
		row.createCell(4).setCellValue(mapValString(map.get("COMBINE")));
		row.createCell(5).setCellValue(mapValString(map.get("customer".toUpperCase())));
		row.createCell(6).setCellValue(mapValString(map.get("type".toUpperCase())));
		row.createCell(7).setCellValue(mapValString(map.get("currency".toUpperCase())));
		row.createCell(8).setCellValue(mapValString(map.get("activity".toUpperCase())));

		row.createCell(11).setCellValue(mapVal(map.get("industry_demand_trend".toUpperCase())));
		row.createCell(12).setCellValue(mapVal(map.get("industry_demand_trend_two".toUpperCase())));
		row.createCell(13).setCellValue(mapVal(map.get("industry_demand_trend_three".toUpperCase())));
		row.createCell(14).setCellValue(mapVal(map.get("industry_demand_trend_four".toUpperCase())));
		row.createCell(15).setCellValue(mapVal(map.get("industry_demand_trend_five".toUpperCase())));

		row.createCell(18).setCellValue(mapVal(map.get("industry_demand_trend_served".toUpperCase())));
		row.createCell(19).setCellValue(mapVal(map.get("industry_demand_trend_served_2".toUpperCase())));
		row.createCell(20).setCellValue(mapVal(map.get("industry_demand_trend_served_3".toUpperCase())));
		row.createCell(21).setCellValue(mapVal(map.get("industry_demand_trend_served_4".toUpperCase())));
		row.createCell(22).setCellValue(mapVal(map.get("industry_demand_trend_served_5".toUpperCase())));

		row.createCell(25).setCellValue(mapVal(map.get("component_usage".toUpperCase())));
		row.createCell(26).setCellValue(mapVal(map.get("component_usage_two".toUpperCase())));
		row.createCell(27).setCellValue(mapVal(map.get("component_usage_three".toUpperCase())));
		row.createCell(28).setCellValue(mapVal(map.get("component_usage_four".toUpperCase())));
		row.createCell(29).setCellValue(mapVal(map.get("component_usage_five".toUpperCase())));

		row.createCell(32).setCellValue(mapVal(map.get("average_sales_price".toUpperCase())));
		row.createCell(33).setCellValue(mapVal(map.get("average_sales_price_two".toUpperCase())));
		row.createCell(34).setCellValue(mapVal(map.get("average_sales_price_three".toUpperCase())));
		row.createCell(35).setCellValue(mapVal(map.get("average_sales_price_four".toUpperCase())));
		row.createCell(36).setCellValue(mapVal(map.get("average_sales_price_five".toUpperCase())));

//		row.createCell(39).setCellValue(mapVal(map.get("total_available_market".toUpperCase())));
//		row.createCell(40).setCellValue(mapVal(map.get("total_available_market_two".toUpperCase())));
//		row.createCell(41).setCellValue(mapVal(map.get("total_available_market_three".toUpperCase())));
//		row.createCell(42).setCellValue(mapVal(map.get("total_available_market_four".toUpperCase())));
//		row.createCell(43).setCellValue(mapVal(map.get("total_available_market_five".toUpperCase())));

//		row.createCell(46).setCellValue(mapVal(map.get("served_available_market".toUpperCase())));
//		row.createCell(47).setCellValue(mapVal(map.get("served_available_market_two".toUpperCase())));
//		row.createCell(48).setCellValue(mapVal(map.get("served_available_market_three".toUpperCase())));
//		row.createCell(49).setCellValue(mapVal(map.get("served_available_market_four".toUpperCase())));
//		row.createCell(50).setCellValue(mapVal(map.get("served_available_market_five".toUpperCase())));

		row.createCell(53).setCellValue(mapVal(map.get("allocation".toUpperCase())));
		row.createCell(54).setCellValue(mapVal(map.get("allocation_two".toUpperCase())));
		row.createCell(55).setCellValue(mapVal(map.get("allocation_three".toUpperCase())));
		row.createCell(56).setCellValue(mapVal(map.get("allocation_four".toUpperCase())));
		row.createCell(57).setCellValue(mapVal(map.get("allocation_five".toUpperCase())));

//		row.createCell(60).setCellValue(mapVal(map.get("revenue".toUpperCase())));
//		row.createCell(61).setCellValue(mapVal(map.get("revenue_two".toUpperCase())));
//		row.createCell(62).setCellValue(mapVal(map.get("revenue_three".toUpperCase())));
//		row.createCell(63).setCellValue(mapVal(map.get("revenue_four".toUpperCase())));
//		row.createCell(64).setCellValue(mapVal(map.get("revenue_five".toUpperCase())));

//		row.createCell(67).setCellValue(mapVal(map.get("quantity".toUpperCase())));
//		row.createCell(68).setCellValue(mapVal(map.get("quantity_two".toUpperCase())));
//		row.createCell(69).setCellValue(mapVal(map.get("quantity_three".toUpperCase())));
//		row.createCell(70).setCellValue(mapVal(map.get("quantity_four".toUpperCase())));
//		row.createCell(72).setCellValue(mapVal(map.get("quantity_five".toUpperCase())));

		row.createCell(98).setCellValue(mapVal(map.get("quantity_month1".toUpperCase())));
		row.createCell(99).setCellValue(mapVal(map.get("quantity_month2".toUpperCase())));
		row.createCell(100).setCellValue(mapVal(map.get("quantity_month3".toUpperCase())));
		row.createCell(101).setCellValue(mapVal(map.get("quantity_month4".toUpperCase())));
		row.createCell(102).setCellValue(mapVal(map.get("quantity_month5".toUpperCase())));
		row.createCell(103).setCellValue(mapVal(map.get("quantity_month6".toUpperCase())));
		row.createCell(104).setCellValue(mapVal(map.get("quantity_month7".toUpperCase())));
		row.createCell(105).setCellValue(mapVal(map.get("quantity_month8".toUpperCase())));
		row.createCell(106).setCellValue(mapVal(map.get("quantity_month9".toUpperCase())));
		row.createCell(107).setCellValue(mapVal(map.get("quantity_month10".toUpperCase())));
		row.createCell(108).setCellValue(mapVal(map.get("quantity_month11".toUpperCase())));
//		row.createCell(109).setCellValue(mapVal(map.get("quantity_month12".toUpperCase())));

		row.createCell(139).setCellValue(mapVal(map.get("price_month1".toUpperCase())));
		row.createCell(140).setCellValue(mapVal(map.get("price_month2".toUpperCase())));
		row.createCell(141).setCellValue(mapVal(map.get("price_month3".toUpperCase())));
		row.createCell(142).setCellValue(mapVal(map.get("price_month4".toUpperCase())));
		row.createCell(143).setCellValue(mapVal(map.get("price_month5".toUpperCase())));
		row.createCell(144).setCellValue(mapVal(map.get("price_month6".toUpperCase())));
		row.createCell(145).setCellValue(mapVal(map.get("price_month7".toUpperCase())));
		row.createCell(146).setCellValue(mapVal(map.get("price_month8".toUpperCase())));
		row.createCell(147).setCellValue(mapVal(map.get("price_month9".toUpperCase())));
		row.createCell(148).setCellValue(mapVal(map.get("price_month10".toUpperCase())));
		row.createCell(149).setCellValue(mapVal(map.get("price_month11".toUpperCase())));
		row.createCell(150).setCellValue(mapVal(map.get("price_month12".toUpperCase())));

	}

	public static String mapVal(Object o){
		if(null == o || o.toString().length()==0){
			return "0";
		}
		return o.toString();
	}

	public static String mapValString(Object o){
		if(null == o || o.toString().length()==0){
			return "";
		}
		return o.toString();
	}

	@SuppressWarnings("rawtypes")
	public static void createCell(XSSFCellStyle style, Object obj,Row row) throws Exception{
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List<String> valueList=new ArrayList<String>();
		for (Field f : fields) {
			if (!Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				valueList.add((String)f.get(obj));
			}
		}
		
		for (int i = 0; i < valueList.size(); i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(valueList.get(i));
			cell.setCellStyle(style);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void createCellParseValueType(XSSFCellStyle style, Object obj,Row row) throws Exception{
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List valueList=new ArrayList();
		for (Field f : fields) {
			if (!Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				valueList.add(f.get(obj));
			}
		}
		
		for (int i = 0; i < valueList.size(); i++) {
			Cell cell = row.createCell(i);
			Object value = valueList.get(i);
			if (value instanceof String) {
				cell.setCellValue((String)value);
			}else if(value instanceof Double){
				cell.setCellValue((double)value);
			}else if(value instanceof Date){
				cell.setCellValue(DateUtil.formatByYyyyMMddHHmmss((Date)value));
			}else if(value instanceof Integer){
				cell.setCellValue((int)value);
			}else if (value instanceof Boolean) {
				cell.setCellValue((boolean) value?"是":"否");
			}
			cell.setCellStyle(style);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void setCell(Map map,Row row) throws Exception{
		row.createCell(0).setCellValue(mapValString(map.get("ENTITY")));
		row.createCell(1).setCellValue(mapValString(map.get("PRODUCT")));
		for(int i=0;i<12;i++){
			row.createCell(i*15+2).setCellValue(mapVal(map.get("MATERIAL_STANDARD_COST"+(i+1))));
			row.createCell(i*15+3).setCellValue(mapVal(map.get("MATERIAL_ADJUST_COST"+(i+1))));
			row.createCell(i*15+4).setCellValue(mapVal(map.get("MATERIAL_COST"+(i+1))));
			row.createCell(i*15+5).setCellValue(mapVal(map.get("STANDARD_HOURS"+(i+1))));
			row.createCell(i*15+6).setCellValue(mapVal(map.get("ADJUST_HOURS"+(i+1))));
			row.createCell(i*15+7).setCellValue(mapVal(map.get("HOURS"+(i+1))));
			row.createCell(i*15+8).setCellValue(mapVal(map.get("MANUAL_STANDARD_RATE"+(i+1))));
			row.createCell(i*15+9).setCellValue(mapVal(map.get("MANUAL_ADJUST_RATE"+(i+1))));
			row.createCell(i*15+10).setCellValue(mapVal(map.get("MANUAL_RATE"+(i+1))));
			row.createCell(i*15+11).setCellValue(mapVal(map.get("MANUAL_COST"+(i+1))));
			row.createCell(i*15+12).setCellValue(mapVal(map.get("MANUFACTURE_STANDARD_RATE"+(i+1))));
			row.createCell(i*15+13).setCellValue(mapVal(map.get("MANUFACTURE_ADJUST_RATE"+(i+1))));
			row.createCell(i*15+14).setCellValue(mapVal(map.get("MANUFACTURE_RATE"+(i+1))));
			row.createCell(i*15+15).setCellValue(mapVal(map.get("MANUFACTURE_COST"+(i+1))));
			row.createCell(i*15+16).setCellValue(mapVal(map.get("UNIT_COST"+(i+1))));
		}

		for(int i=12;i<16;i++){
			row.createCell(i*15+2).setCellValue(mapVal(map.get("MATERIAL_STANDARD_COST_YEAR_"+(i-11))));
			row.createCell(i*15+3).setCellValue(mapVal(map.get("MATERIAL_ADJUST_COST_YEAR_"+(i-11))));
			row.createCell(i*15+4).setCellValue(mapVal(map.get("MATERIAL_COST_YEAR_"+(i-11))));
			row.createCell(i*15+5).setCellValue(mapVal(map.get("STANDARD_HOURS_YEAR_"+(i-11))));
			row.createCell(i*15+6).setCellValue(mapVal(map.get("ADJUST_HOURS_YEAR_"+(i-11))));
			row.createCell(i*15+7).setCellValue(mapVal(map.get("HOURS_YEAR_"+(i-11))));
			row.createCell(i*15+8).setCellValue(mapVal(map.get("MANUAL_STANDARD_RATE_YEAR_"+(i-11))));
			row.createCell(i*15+9).setCellValue(mapVal(map.get("MANUAL_ADJUST_RATE_YEAR_"+(i-11))));
			row.createCell(i*15+10).setCellValue(mapVal(map.get("MANUAL_RATE_YEAR_"+(i-11))));
			row.createCell(i*15+11).setCellValue(mapVal(map.get("MANUAL_COST_YEAR_"+(i-11))));
			row.createCell(i*15+12).setCellValue(mapVal(map.get("MANUFACTURE_STANDARD_RATE_Y"+(i-11))));
			row.createCell(i*15+13).setCellValue(mapVal(map.get("MANUFACTURE_ADJUST_RATE_YEAR_"+(i-11))));
			row.createCell(i*15+14).setCellValue(mapVal(map.get("MANUFACTURE_RATE_YEAR_"+(i-11))));
			row.createCell(i*15+15).setCellValue(mapVal(map.get("MANUFACTURE_COST_YEAR_"+(i-11))));
			row.createCell(i*15+16).setCellValue(mapVal(map.get("UNIT_COST_YEAR_"+(i-11))));
		}

	}
	
	@SuppressWarnings("rawtypes")
	public static void createCell(XSSFCellStyle style, Object obj,Row row,int indent) throws Exception{
		if (indent<0) {
			indent=0;
		}
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List<String> valueList=new ArrayList<String>();
		for (Field f : fields) {
			if (!Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				Object value = f.get(obj);
				valueList.add(value==null?"":value.toString());
			}
		}
		
		for (int i = 0; i < valueList.size()-indent; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(valueList.get(i));
			if (style!=null) {
				cell.setCellStyle(style);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void createCell(XSSFCellStyle lockStyle,int lockIndex,XSSFCellStyle unlockStyle, Object obj,Row row,int indent) throws Exception{
		if (indent<0) {
			indent=0;
		}
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List<String> valueList=new ArrayList<String>();
		for (Field f : fields) {
			if (!Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				Object value = f.get(obj);
				valueList.add(value==null?"":value.toString());
			}
		}
		
		for (int i = 0; i < valueList.size()-indent; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(valueList.get(i));
			if (i<lockIndex) {
				cell.setCellStyle(lockStyle);
			}else {
				cell.setCellStyle(unlockStyle);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void createCell(XSSFCellStyle lockStyle,int[] lockIndex,XSSFCellStyle unlockStyle, Object obj,Row row,int indent) throws Exception{
		if (indent<0) {
			indent=0;
		}
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List<String> valueList=new ArrayList<String>();
		for (Field f : fields) {
			if (!Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				Object value = f.get(obj);
				valueList.add(value==null?"":value.toString());
			}
		}
		
		for (int i = 0; i < valueList.size()-indent; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(valueList.get(i));
			boolean match=false;
			for (int j = 0; j < lockIndex.length; j++) {
				if (lockIndex[j]==i) {
					match=true;
					break;
				}
			}
			if (match) {
				cell.setCellStyle(lockStyle);
			}else {
				cell.setCellStyle(unlockStyle);
			}
		}
	}
	
}
