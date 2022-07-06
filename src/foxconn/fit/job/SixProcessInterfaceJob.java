package foxconn.fit.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import foxconn.fit.service.base.UserLogService;
import foxconn.fit.util.ExceptionUtil;

public class SixProcessInterfaceJob {

	private Log logger = LogFactory.getLog(this.getClass());
	
	private String sendUrl;
	private String callbackUrl;
	
	@Autowired
	private UserLogService userLogService;
	
	public void sendAlert(){
		try {
			logger.info("=========================开始执行预警发送任务============================");
			List<Object[]> list = userLogService.listAlarm();
			if (list!=null && list.size()>0) {
				for (Object[] values : list) {
					String id = values[0].toString();
					String system = values[1].toString();
					String dept = values[2].toString();
					String light = values[3].toString();
					String subject = values[4].toString();
					String body = values[5].toString();
					String reporttime = values[6].toString();
					String reportvalue = values[7].toString();
					String reportUrl=callbackUrl+"/unauth/alarmCallback?id="+id;
					String success="FALSE";
					String msg="";
					try {
						CloseableHttpClient httpclient = HttpClients.createDefault();
						HttpPost httpPost = new HttpPost(sendUrl);
						Map<String,String> map=new HashMap<String, String>();
						map.put("System", system);
						map.put("Dept", dept);
						map.put("Light", light);
						map.put("Subject", subject);
						map.put("Body", body);
						map.put("ReportTime", reporttime);
						map.put("ReportValue", reportvalue);
						map.put("ReportURL", reportUrl);
						String param=JSONObject.fromObject(map).toString();
						logger.info("发送预警接口参数==========================================================");
						logger.info(param);
						httpPost.setEntity(new StringEntity(param,"UTF-8"));
						httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
						httpPost.addHeader("Accept-Encoding", "gzip, deflate");
						httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
						httpPost.addHeader("Content-Type", "application/json");
						
						CloseableHttpResponse response = httpclient.execute(httpPost);

						HttpEntity entity = response.getEntity();
						String result = EntityUtils.toString(entity,"UTF-8");
						logger.info("接收预警接口结果==============================================================");
						logger.info("【ID = "+id+"】--> "+result);
						com.alibaba.fastjson.JSONObject parseObject = com.alibaba.fastjson.JSONObject.parseObject(result);
						String isOK = parseObject.getString("IsOK");
						if ("0".equals(isOK)) {
							success="TRUE";
						}else{
							msg=parseObject.getString("Msg");
						}
					} catch (Exception e) {
						logger.error("发送预警接口失败【ID = "+id+"】 : ", e);
						msg=ExceptionUtil.getRootCauseMessage(e);
					}
					
					try {
						userLogService.updateSIXP_INTERFACE(id,success,reportUrl);
					} catch (Exception e) {
						logger.error("保存预警接口信息失败【ID = "+id+"】 : ", e);
					}
					
					if ("FALSE".equals(success)) {
						//发送邮件给管理员
						try {
							logger.info("发送预警邮件给管理员【ID = "+id+"】: "+msg);
							userLogService.sendAlarmMail(msg);
						} catch (Exception e) {
							logger.error("发送预警邮件给管理员【ID = "+id+"】失败 : ", e);
						}
					}
				}
			}
			logger.info("=========================执行预警发送任务结束============================");
		} catch (Exception e) {
			logger.error("=========================执行预警发送任务失败============================",e);
		}
	}

	public void setSendUrl(String sendUrl) {
		this.sendUrl = sendUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

}
