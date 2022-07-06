package foxconn.fit.job;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import foxconn.fit.service.base.UserLogService;

public class CleanDownloadFileJob {

	private Log logger = LogFactory.getLog(this.getClass());
	
	private String directory;
	@Autowired
	private UserLogService userLogService;
	
	public void clean(){
		try {
			logger.info("=========================开始清理下载文件============================");
			File dir=new File(directory);
			if (dir.exists()) {
				File[] files = dir.listFiles();
				if (files!=null && files.length>0) {
					for (File file : files) {
						file.delete();
					}
				}
			}
			logger.info("=========================清理下载文件结束============================");
		} catch (Exception e) {
			logger.error("=========================清理下载文件失败============================",e);
		}
		try {
			logger.info("=========================开始清理6个月前用户日志============================");
			userLogService.cleanHistory();
			logger.info("=========================清理6个月前用户日志结束============================");
		} catch (Exception e) {
			logger.error("=========================清理下载文件失败============================",e);
		}
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

}
