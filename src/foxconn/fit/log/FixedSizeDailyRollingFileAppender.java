package foxconn.fit.log;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

import foxconn.fit.util.DateUtil;

public class FixedSizeDailyRollingFileAppender extends FileAppender {
	//指定大小滚动
	protected long maxFileSize;
	protected int maxBackupIndex;
	//指定间隔时间滚动
	private String datePattern;
	private String baseFilename;
	private String scheduledFilename;
	private long nextCheck;
	Date now;
	SimpleDateFormat sdf;
	RollCalendar rc;
	int checkPeriod;
	static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");

	public FixedSizeDailyRollingFileAppender() {
		maxFileSize = 10485760L;
        maxBackupIndex = 1;
		datePattern = "'_'yyyy-MM-dd HH'.log'";
		nextCheck = System.currentTimeMillis() - 1L;
		now = new Date();
		rc = new RollCalendar();
		checkPeriod = -1;
	}

	public FixedSizeDailyRollingFileAppender(Layout layout, String filename,
			String datePattern) throws IOException {
		super(layout, filename, true);
		maxFileSize = 10485760L;
        maxBackupIndex = 1;
		this.datePattern ="'_'yyyy-MM-dd HH'.log'";
		nextCheck = System.currentTimeMillis() - 1L;
		now = new Date();
		rc = new RollCalendar();
		checkPeriod = -1;
		this.datePattern = datePattern;
		activateOptions();
	}

	public void activateOptions() {
		if (datePattern != null && super.fileName != null) {
			now.setTime(System.currentTimeMillis());
			sdf = new SimpleDateFormat(datePattern);
			int type = computeCheckPeriod();
			printPeriodicity(type);
			rc.setType(type);
			scheduledFilename = super.fileName+ sdf.format(new Date());
			baseFilename=super.fileName;
			super.fileName=scheduledFilename;
		} else {
			LogLog.error("Either File or DatePattern options are not set for appender ["+ super.name + "].");
		}
		super.activateOptions();
	}

	void printPeriodicity(int type) {
		switch (type) {
		case 0: // '\0'
			LogLog.debug("Appender [" + super.name+ "] to be rolled every minute.");
			break;

		case 1: // '\001'
			LogLog.debug("Appender [" + super.name+ "] to be rolled on top of every hour.");
			break;

		case 2: // '\002'
			LogLog.debug("Appender [" + super.name+ "] to be rolled at midday and midnight.");
			break;

		case 3: // '\003'
			LogLog.debug("Appender [" + super.name+ "] to be rolled at midnight.");
			break;

		case 4: // '\004'
			LogLog.debug("Appender [" + super.name+ "] to be rolled at start of week.");
			break;

		case 5: // '\005'
			LogLog.debug("Appender [" + super.name+ "] to be rolled at start of every month.");
			break;

		default:
			LogLog.warn("Unknown periodicity for appender [" + super.name+ "].");
			break;
		}
	}

	int computeCheckPeriod() {
		RollCalendar rollingCalendar = new RollCalendar(gmtTimeZone,
				Locale.ENGLISH);
		Date epoch = new Date(0L);
		if (datePattern != null) {
			for (int i = 0; i <= 5; i++) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						datePattern);
				simpleDateFormat.setTimeZone(gmtTimeZone);
				String r0 = simpleDateFormat.format(epoch);
				rollingCalendar.setType(i);
				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
				String r1 = simpleDateFormat.format(next);
				if (r0 != null && r1 != null && !r0.equals(r1))
					return i;
			}

		}
		return -1;
	}

	void rollDailyOver() throws IOException {
		if (datePattern == null) {
			super.errorHandler.error("Missing DatePattern option in rollDailyOver().");
			return;
		}
		String datedFilename = this.baseFilename + sdf.format(now);
		if (scheduledFilename.equals(datedFilename))
			return;
		closeFile();
		File target = new File(datedFilename);
        if(target.exists())
            target.delete();
		try {
			setFile(datedFilename, false, super.bufferedIO, super.bufferSize);
		} catch (IOException ioexception) {
			super.errorHandler.error("setFile(" + super.fileName+ ", false) call failed.");
		}
		scheduledFilename = datedFilename;
	}

	public void rollFixedSizeOver() throws IOException {
		LogLog.debug("rollFixedSizeOver over count="+ ((CountingQuietWriter) super.qw).getCount());
		LogLog.debug("maxBackupIndex=" + maxBackupIndex);
		if (maxBackupIndex > 0) {
			File file = new File(scheduledFilename + '.' + maxBackupIndex);
			if (file.exists())
				file.delete();
			File target;
			for (int i = maxBackupIndex - 1; i >= 1; i--) {
				file = new File(scheduledFilename + "." + i);
				if (file.exists()) {
					target = new File(scheduledFilename + '.' + (i + 1));
					LogLog.debug("Renaming file " + file + " to " + target);
					file.renameTo(target);
				}
			}

			target = new File(scheduledFilename + "." + 1);
			closeFile();
			file = new File(scheduledFilename);
			LogLog.debug("Renaming file " + file + " to " + target);
			file.renameTo(target);
		}
		try {
			setFile(scheduledFilename, false, super.bufferedIO, super.bufferSize);
		} catch (IOException e) {
			LogLog.error("setFile(" + scheduledFilename + ", false) call failed.",e);
		}
	}

	public synchronized void setFile(String fileName, boolean append,
			boolean bufferedIO, int bufferSize) throws IOException {
		super.setFile(fileName, append, super.bufferedIO, super.bufferSize);
		if (append) {
			File f = new File(fileName);
			((CountingQuietWriter) super.qw).setCount(f.length());
		}
	}
	
	protected void subAppend(LoggingEvent event) {
		long n = System.currentTimeMillis();
		
		if ((n >= nextCheck && ((CountingQuietWriter)super.qw).getCount() >= maxFileSize)) {
			now.setTime(n);
			nextCheck = rc.getNextCheckMillis(now);
			try {
				rollDailyOver();
			} catch (IOException ioe) {
				LogLog.error("rollDailyOver() failed.", ioe);
			}
		}else if(super.fileName != null && ((CountingQuietWriter)super.qw).getCount() >= maxFileSize){
			try {
				rollFixedSizeOver();
			} catch (IOException e) {
				LogLog.error("rollFixedSizeOver() failed.", e);
			}
		}else {
			//新的一天强制滚动
			boolean newDay=true;
			try {
				newDay = DateUtil.isLogNewDay(nextCheck, n);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (newDay) {
				now.setTime(n);
				nextCheck = rc.getNextCheckMillis(now);
				try {
					rollDailyOver();
				} catch (IOException ioe) {
					LogLog.error("rollDailyOver() failed.", ioe);
				}
			}
		}
		
		super.subAppend(event);
	}
	
	public void setDatePattern(String pattern) {
		datePattern = pattern;
	}

	public String getDatePattern() {
		return datePattern;
	}
	
    public int getMaxBackupIndex() {
		return maxBackupIndex;
	}

	public long getMaximumFileSize() {
		return maxFileSize;
	}

	public void setMaxBackupIndex(int maxBackups) {
		maxBackupIndex = maxBackups;
	}

	public void setMaximumFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public void setMaxFileSize(String value) {
		maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1L);
	}

	protected void setQWForFiles(Writer writer) {
		super.qw = new CountingQuietWriter(writer, super.errorHandler);
	}

}