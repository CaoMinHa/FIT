package foxconn.fit.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * 时间类
 * 
 * @author chenliang
 * 
 */
public class DateUtil {

	public static final SimpleDateFormat SDF_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SDF_yyyy_MM = new SimpleDateFormat("yyyy-MM");
	public static final SimpleDateFormat SDF_yyyyMM = new SimpleDateFormat("yyyyMM");
	public static final SimpleDateFormat SDF_yyyy = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat SDF_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat SDF_ddSMMSyyyy = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat SDF_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat HHmmss = new SimpleDateFormat("HH:mm:ss");

	/**
	 * 判断是否是新的一天
	 * @param oldTime
	 * @param newTime
	 * @return
	 * @throws Exception
	 */
	public static boolean isLogNewDay(long oldTime, long newTime) throws Exception {
		Date oldDate = SDF_yyyy_MM_dd.parse(SDF_yyyy_MM_dd.format(new Date(oldTime)));
		Date newDate = SDF_yyyy_MM_dd.parse(SDF_yyyy_MM_dd.format(new Date(newTime)));

		return oldTime==newDate.getTime() || newDate.after(oldDate);
	}
	
	public static String formatByYyyy_MM_dd(Date date){
		return SDF_yyyy_MM_dd.format(date);
	}
	
	public static String formatByYyyy_MM_dd(Date date,int month){
		if (date!=null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, month);
			return SDF_yyyy_MM_dd.format(calendar.getTime());
		}
		return "";
	}
	
	public static String formatByddSMMSyyyy(Date date){
		if (date!=null) {
			return SDF_ddSMMSyyyy.format(date);
		}
		return "";
	}
	
	public static String formatByYyyyMMdd(Date date){
		if (date!=null) {
			return SDF_yyyyMMdd.format(date);
		}
		return "";
	}
	
	public static String formatByHHmmss(Date date){
		if (date!=null) {
			return HHmmss.format(date);
		}
		return "";
	}
	
	public static String formatByYyyyMMddHHmmss(Date date){
		if (date!=null) {
			return SDF_yyyyMMddHHmmss.format(date);
		}
		return "";
	}
	
	public static String formatByMinYyyyMMddHHmmss(Date date){
		if (date!=null) {
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			
			return SDF_yyyyMMddHHmmss.format(calendar.getTime());
		}
		return "";
	}
	
	public static String formatByMaxYyyyMMddHHmmss(Date date){
		if (date!=null) {
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			
			return SDF_yyyyMMddHHmmss.format(calendar.getTime());
		}
		return "";
	}
	
	public static Date parseByYyyyMMddHHmmss(String dateString){
		if (StringUtils.isNotEmpty(dateString)) {
			try {
				return SDF_yyyyMMddHHmmss.parse(dateString);
			} catch (Exception e) {
				return null;
			}
		}
		
		return null;
	}
	
	public static Date parseByYyyy_MM(String dateString){
		if (StringUtils.isNotEmpty(dateString)) {
			try {
				return SDF_yyyy_MM.parse(dateString);
			} catch (Exception e) {
				return null;
			}
		}
		
		return null;
	}
	
	public static Date parseByYyyyMM(String dateString){
		if (StringUtils.isNotEmpty(dateString)) {
			try {
				return SDF_yyyyMM.parse(dateString);
			} catch (Exception e) {
				return null;
			}
		}
		
		return null;
	}
	
	public static Date parseByYyyy(String dateString){
		if (StringUtils.isNotEmpty(dateString)) {
			try {
				return SDF_yyyy.parse(dateString);
			} catch (Exception e) {
				return null;
			}
		}
		
		return null;
	}
	
	public static Date parseByYyyy_MM_dd(String dateString){
		if (StringUtils.isNotEmpty(dateString)) {
			try {
				return SDF_yyyy_MM_dd.parse(dateString);
			} catch (Exception e) {
				return null;
			}
		}
		
		return null;
	}
	
	public static Date parseByYyyyMMdd(String dateString){
		if (StringUtils.isNotEmpty(dateString)) {
			try {
				return SDF_yyyyMMdd.parse(dateString);
			} catch (Exception e) {
				return null;
			}
		}
		
		return null;
	}
	
	public static String checkDateByyyyyMMdd(String dateStr){
		try {
			if (StringUtils.isEmpty(dateStr) || dateStr.length()!=8) {
				return null;
			}
			
			Date date = SDF_yyyyMMdd.parse(dateStr);
			
			return SDF_yyyyMMdd.format(date);
		} catch (Exception e) {
			return null;
		}
		
	}

	public static Date getMonthFirst(Date month) {
		if (month!=null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(month);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			return calendar.getTime();
		}
		return null;
	}

	public static Date getMonthLast(Date month) {
		if (month!=null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(month);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			return calendar.getTime();
		}
		return null;
	}
	
}
