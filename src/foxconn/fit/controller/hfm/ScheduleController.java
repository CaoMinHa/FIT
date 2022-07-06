package foxconn.fit.controller.hfm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import foxconn.fit.advice.Log;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumScheduleType;
import foxconn.fit.entity.ebs.Parameter;
import foxconn.fit.service.ebs.ParameterService;
import foxconn.fit.service.hfm.ScheduleService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import foxconn.fit.util.UnicodeUtil;

@Controller
@RequestMapping("/hfm/schedule")
@SuppressWarnings("deprecation")
public class ScheduleController{

	public static org.apache.commons.logging.Log logger = LogFactory.getLog(ScheduleController.class);
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private ParameterService parameterService;
	
	@Value("${uri}")
	private String URI;
	
	@Value("${username}")
	private String USERNAME;
	
	@Value("${password}")
	private String PASSWORD;
	
	private String JSESSIONID;
	
	@PostConstruct
	public void loginHFM(){
		try {
			hfmLogin();
		} catch (Exception e) {
			logger.error("登录HFM失败:", e);
		}
	}
	
	public void hfmLogin() throws Exception{
		logger.info("登陆hfm================================================开始");
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URI+"/workspace/logon");
		
		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
		httpPost.addHeader("Accept", "*/*");
		httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		String param="sso_username="+USERNAME+"&sso_password="+PASSWORD+"&LOCALE_LANGUAGE=%20&rightToLeft=%20&accessibilityMode=%20&themeSelection=%20";
		httpPost.setEntity(new StringEntity(param,HTTP.UTF_8));

		CloseableHttpResponse response = httpclient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity,HTTP.UTF_8);
		logger.info("login:结果==============================================================");
		logger.info(result.toString());
		int beginIndex = result.indexOf("<token><![CDATA[")+16;
		int endIndex = result.indexOf("]]></token>");
		String sso_token = result.substring(beginIndex, endIndex);
		logger.info("sso_token:"+sso_token);
		logger.info("login:结果==============================================================");
		
		response.close();
		
		HttpPost httpPost1 = new HttpPost(URI+"/awb/modules/com/hyperion/awb/web/datasync/Adf.do");
		
		httpPost1.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
		httpPost1.addHeader("Accept", "*/*");
		httpPost1.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpPost1.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		String param1="sso_token="+URLEncoder.encode(sso_token);
		httpPost1.setEntity(new StringEntity(param1,HTTP.UTF_8));

		CloseableHttpResponse response1 = httpclient.execute(httpPost1);
		
		Header[] headers = response1.getAllHeaders();
		for (Header header : headers) {
			String value = header.getValue();
			if (value.startsWith("JSESSIONID=")) {
				JSESSIONID = value.substring(11, value.indexOf(";"));
				break;
			}
		}
		
		response1.close();
		
		logger.info("登陆hfm================================================成功");
	}
	
	@RequestMapping(value = "getJobsElement")
	@ResponseBody
	public String getJobsElement(AjaxResult result,String jobId) {
		try {
			jobDetail(result, jobId);
		} catch (Exception e) {
			try {
				hfmLogin();
			} catch (Exception e2) {
				logger.error("登录HFM失败:", e2);
				result.put("flag", "fail");
				result.put("msg", "登录HFM失败:"+ExceptionUtil.getRootCauseMessage(e2));
				return result.getJson();
			}
			
			try {
				jobDetail(result, jobId);
			} catch (Exception e1) {
				logger.error("获取job详情失败:", e);
			}
		}
		
		return result.getJson();
	}

	private void jobDetail(AjaxResult result, String jobId) throws IOException,ClientProtocolException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URI+"/awb/jobstask.getJobsElement.do");
		
		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
		httpPost.addHeader("Accept", "*/*");
		httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpPost.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
		
		String param="jobId="+jobId+"&totalRecords=50&statusFilter&folderId=-1&typeFilter=512&startDate=&endDate=&startRecordNumber=1&selectedRow=0&selectedGuid=30&LOCALE_LANGUAGE=zh_CN&rightToLeft=false&accessibilityMode=false&themeSelection=Skyros";
		httpPost.setEntity(new StringEntity(param,HTTP.UTF_8));
		
		CloseableHttpResponse response = httpclient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		String string = UnicodeUtil.decodeUnicode(EntityUtils.toString(entity,HTTP.UTF_8));
		logger.info("getJobsElement:结果==============================================================");
		logger.info(string);
		logger.info("getJobsElement:结果==============================================================");
		
		JSONArray jsonArray = JSONObject.parseArray(string);
		JSONObject object = (JSONObject) jsonArray.get(0);
		JSONArray jobsArr = object.getJSONArray("jobsArr");
		JSONObject obj = jobsArr.getJSONObject(0);
		String id=obj.getString("id");
		String description=obj.getString("description");
		Long LastUpdatedTime = obj.getLong("LastUpdatedTime");
		SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String lastUpdatedTime=format.format(new Date(LastUpdatedTime));
		String status=obj.getString("status");
		String userIdentification=obj.getString("userIdentification");
		
		result.put("id", id);
		result.put("description", description);
		result.put("lastUpdatedTime", lastUpdatedTime);
		result.put("status", status);
		result.put("userIdentification", userIdentification);
	}
	
	@RequestMapping(value = "dataSynchronize")
	@ResponseBody
	@Log(name = "HFM数据同步")
	public String dataSynchronize(HttpServletRequest request,HttpServletResponse response, AjaxResult result,
			@Log(name = "同步任务类型") String type,@Log(name = "年月") String month,@Log(name = "法人编码") String code) {
		result.put("msg", "数据同步成功");
		try {
			Assert.hasText(type, "同步任务类型不能为空(Type Not Null)");
			
			Date date = DateUtil.parseByYyyy_MM(month);
			Assert.notNull(date, "年月格式错误(Error Date Formats)");
			
			String[] split = month.split("-");
			int year = Integer.parseInt(split[0]);
			int period = Integer.parseInt(split[1]);
			
			Assert.hasText(code, "法人编码不能为空(Entity Code Not Null)");
			
			if (code.endsWith(",")) {
				code=code.substring(0, code.length()-1);
			}
			String[] codes = code.split(",");
			
			String corporationCode = SecurityUtils.getCorporationCode();
			String[] tarCodes = corporationCode.split(",");
			
			List<String> tarList=new ArrayList<String>();
			for (String string : tarCodes) {
				tarList.add(string.substring(2));
			}
			
			List<String> list=new ArrayList<String>();
			
			for (String string : codes) {
				if (tarList.contains(string)) {
					list.add(string);
				}
			}
			
			Assert.isTrue(!list.isEmpty(), "法人编码错误(Error Entity Code)");
			
			String message = scheduleService.dataTransfer(list,year,period,EnumScheduleType.valueOf(type));
			if (StringUtils.isNotEmpty(message)) {
				result.put("flag", "fail");
				result.put("msg", message);
				return result.getJson();
			}
			
			try {
				schedule(result);
			} catch (Exception e) {
				try {
					hfmLogin();
				} catch (Exception e1) {
					logger.error("登录HFM失败:", e1);
					result.put("flag", "fail");
					result.put("msg", "登录HFM失败:"+ExceptionUtil.getRootCauseMessage(e1));
					return result.getJson();
				}
				
				schedule(result);
			}
		} catch (Exception e) {
			logger.error("数据同步失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}
	
	@RequestMapping(value = "esbDataSynchronize")
	@ResponseBody
	@Log(name = "EBS数据同步")
	public String esbDataSynchronize(HttpServletRequest request,HttpServletResponse response, AjaxResult result,
			@Log(name = "同步任务类型") String type,@Log(name = "年月") String date,@Log(name = "公司编码") String entitys) {
		result.put("msg", "数据同步成功");
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(type, "同步任务类型不能为空");
			
			Date dt = DateUtil.parseByYyyy_MM(date);
			Assert.notNull(dt, "年月格式错误");
			
			String[] split = date.split("-");
			String year = split[0];
			String period = split[1];
			if (period.length()<2) {
				period="0"+period;
			}
			
			Assert.hasText(entitys, "公司编码不能为空");
			
			if (entitys.endsWith(",")) {
				entitys=entitys.substring(0, entitys.length()-1);
			}
			String[] codes = entitys.split(",");
			List<String> tarList=new ArrayList<String>();
			String entity1 = SecurityUtils.getEBS();
			if (StringUtils.isNotEmpty(entity1)) {
				for (String string : entity1.split(",")) {
					tarList.add(string.substring(2));
				}
			}
			List<String> entityList=new ArrayList<String>();
			for (String string : codes) {
				if (tarList.contains(string)) {
					entityList.add(string);
				}
			}
			
			Assert.isTrue(!entityList.isEmpty(), "公司编码错误");
			
			String entityStr="";
			for (String entity : entityList) {
				entityStr=entityStr+entity+",";
			}
			entityStr=entityStr.substring(0, entityStr.length()-1);
			String message = scheduleService.dataMapping(entityStr,year,period,type,locale);
			if (StringUtils.isNotEmpty(message)) {
				result.put("flag", "fail");
				result.put("msg", message);
				return result.getJson();
			}
			
			try {
				scheduleEbs(result);
			} catch (Exception e) {
				try {
					hfmLogin();
				} catch (Exception e1) {
					logger.error("登录HFM失败:", e1);
					result.put("flag", "fail");
					result.put("msg", "登录HFM失败:"+ExceptionUtil.getRootCauseMessage(e1));
					return result.getJson();
				}
				
				scheduleEbs(result);
			}
		} catch (Exception e) {
			logger.error("数据同步失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}

	private void schedule(AjaxResult result) throws Exception {
		String id = enumBaseDataSyncDefinitions();
		if (StringUtils.isEmpty(id)) {
			result.put("flag", "fail");
			result.put("msg", "请先在HFM配置同步任务");
		}else{
			String jobId = execSync(id);
			result.put("jobId", jobId);
		}
	}
	
	private void scheduleEbs(AjaxResult result) throws Exception {
		Parameter task = parameterService.get("taskId");
		if (task==null) {
			result.put("flag", "fail");
			result.put("msg", "请先在接口平台配置同步任务");
		}else{
			String jobId = execEBSSync(task.getValue());
			result.put("jobId", jobId);
		}
	}
	
	private String enumBaseDataSyncDefinitions() throws Exception {
		String id = null;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URI+"/awb/datasync.enumBaseDataSyncDefinitions.do");
		
		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
		httpPost.addHeader("Accept", "*/*");
		httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpPost.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
		
		CloseableHttpResponse response = httpclient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity,HTTP.UTF_8);
		logger.info("enumDefinitions:结果==============================================================");
		logger.info(result.toString());
		logger.info("enumDefinitions:结果==============================================================");
		
		JSONArray jsonArray = JSONObject.parseArray(result);
		for (Object object : jsonArray) {
			JSONObject jsonObject=(JSONObject) object;
			String type = jsonObject.getString("type");
			if ("Synchronization".equals(type)) {
				id = jsonObject.getString("ID");
				break;
			}
		}
		
		response.close();
		
		return id;
	}
	
	private String execSync(String id) throws Exception {
		String jobId = "";
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URI+"/awb/datasync.dme.execSync.do");
		
		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
		httpPost.addHeader("Accept", "*/*");
		httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpPost.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
		
		String param="id="+id+"&dataTransformationOperator=&dataTransformationValue=&externalFileUrl&validateOnly=false&fileUploaded=false&dataLoadOption=dataSyncLoadOptionHfmMode%3Dmerge%3BdataSyncLoadOptionHfmAccummulateInFile%3Dfalse";
		httpPost.setEntity(new StringEntity(param,HTTP.UTF_8));

		CloseableHttpResponse response = httpclient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity,HTTP.UTF_8);
		logger.info("execSync:结果==============================================================");
		logger.info(result.toString());
		logger.info("execSync:结果==============================================================");
		
		if (result.endsWith(";")) {
			result=result.substring(0, result.length()-1);
		}
		JSONObject jsonObject = JSONObject.parseObject(result);
		jobId = jsonObject.getString("jobId");
		
		response.close();
		
		return jobId;
	}
	
	private String execEBSSync(String id) throws Exception {
		String jobId = "";
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URI+"/awb/datasync.dme.execSync.do");
		
		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
		httpPost.addHeader("Accept", "*/*");
		httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpPost.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
		
		String param="id="+id+"&dataTransformationOperator=&dataTransformationValue=&externalFileUrl&validateOnly=false&fileUploaded=false&dataLoadOption=dataSyncLoadOptionHfmMode%3Dmerge%3BdataSyncLoadOptionHfmAccummulateInFile%3Dtrue";
		httpPost.setEntity(new StringEntity(param,HTTP.UTF_8));

		CloseableHttpResponse response = httpclient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity,HTTP.UTF_8);
		logger.info("execEBSSync:结果==============================================================");
		logger.info(result.toString());
		logger.info("execEBSSync:结果==============================================================");
		
		if (result.endsWith(";")) {
			result=result.substring(0, result.length()-1);
		}
		JSONObject jsonObject = JSONObject.parseObject(result);
		jobId = jsonObject.getString("jobId");
		
		response.close();
		
		return jobId;
	}
	
	@RequestMapping(value = "dataSynchronizeLog")
	@ResponseBody
	public String dataSynchronizeLog(AjaxResult result,String jobId,String type) {
		result.put("msg", "查询数据同步日志成功");
		try {
			Assert.hasText(jobId, "任务ID不能为空");
			Assert.isTrue("Synchronization".equals(type) || "Destination".equals(type), "错误的日志类型");
			
			try {
				getJobLog(result, jobId,type);
			} catch (Exception e) {
				try {
					hfmLogin();
				} catch (Exception e1) {
					logger.error("登录HFM失败:", e1);
					result.put("flag", "fail");
					result.put("msg", "登录HFM失败:"+ExceptionUtil.getRootCauseMessage(e1));
					return result.getJson();
				}
				
				getJobLog(result, jobId,type);
			}
		} catch (Exception e) {
			logger.error("查询数据同步日志失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}
	
	@RequestMapping(value = "listSynchronizeTask")
	@ResponseBody
	public String listSynchronizeTask(AjaxResult result) {
		result.put("msg", "查询同步任务成功");
		try {
			try {
				listTask(result);
			} catch (Exception e) {
				try {
					hfmLogin();
				} catch (Exception e1) {
					logger.error("登录HFM失败:", e1);
					result.put("flag", "fail");
					result.put("msg", "登录HFM失败:"+ExceptionUtil.getRootCauseMessage(e1));
					return result.getJson();
				}
				
				listTask(result);
			}
		} catch (Exception e) {
			logger.error("查询同步任务失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}

		return result.getJson();
	}

	private void listTask(AjaxResult result) throws IOException, ClientProtocolException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URI+"/awb/datasync.enumBaseDataSyncDefinitions.do");
		
		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
		httpPost.addHeader("Accept", "*/*");
		httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpPost.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
		
		CloseableHttpResponse response = httpclient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		String msg = EntityUtils.toString(entity,HTTP.UTF_8);
		logger.info("enumDefinitions:结果==============================================================");
		logger.info(msg.toString());
		logger.info("enumDefinitions:结果==============================================================");
		
		List<String> idList=new ArrayList<String>();
		List<String> nameList=new ArrayList<String>();
		
		JSONArray jsonArray = JSONObject.parseArray(msg);
		for (Object object : jsonArray) {
			JSONObject jsonObject=(JSONObject) object;
			String type = jsonObject.getString("type");
			if ("Synchronization".equals(type)) {
				idList.add(jsonObject.getString("ID"));
				nameList.add(jsonObject.getString("name"));
			}
		}
		
		result.put("idList", idList);
		result.put("nameList", nameList);
		
		response.close();
	}

	private void getJobLog(AjaxResult result, String jobId,String type) throws IOException, ClientProtocolException, UnsupportedEncodingException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URI+"/awb/jobstask.getJobsElement.do");
		
		httpPost.addHeader("Accept", "*/*");
		httpPost.addHeader("Accept-Encoding", "gzip, deflate");
		httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpPost.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
		
		String param="userId=&jobId="+jobId+"&jobTypes=DataSynchronization&jobStatus=&filterBySelf=true&reload=false&showHidden=false&startRecordNumber=1&totalRecords=50&statusFilter&folderId=-1&typeFilter=512&startDate=&endDate=&LOCALE_LANGUAGE=zh_CN&rightToLeft=false&accessibilityMode=false&themeSelection=Skyros";
		httpPost.setEntity(new StringEntity(param,"UTF-8"));
		
		CloseableHttpResponse response = httpclient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		String message = UnicodeUtil.decodeUnicode(EntityUtils.toString(entity,"UTF-8"));
		logger.info("查询日志结果==============================================================");
		logger.info(message);
		logger.info("查询日志结果==============================================================");
		JSONArray array1 = JSONObject.parseArray(message);
		JSONObject object1 = (JSONObject) array1.get(0);
		JSONArray array2 = (JSONArray) object1.get("jobsArr");
		JSONObject object2 = (JSONObject) array2.get(0);
		JSONArray array3 = (JSONArray) object2.get("Attachments");
		String attachmentId="";
		String attachmentName="";
		if("Synchronization".equals(type)){
			for (Object obj : array3) {
				JSONObject attachment=(JSONObject)obj;
				String name = attachment.getString("name").trim();
				if("Data Synchronization Log".equals(name) || "数据同步日志".equals(name)){
					attachmentId = attachment.getString("id");
					attachmentName=URLEncoder.encode(name,"UTF-8");
					break;
				}
			}
		}else{
			for (Object obj : array3) {
				JSONObject attachment=(JSONObject)obj;
				String name = attachment.getString("name").trim();
				if ("Destination Log".equals(name) || "目标日志".equals(name)) {
					attachmentId = attachment.getString("id");
					attachmentName=URLEncoder.encode(name,"UTF-8");
					break;
				}
			}
		}
		
		response.close();
		
		HttpPost httpPost1 = new HttpPost(URI+"/awb/jobstask.getJobBatchAttachment.do");
		
		httpPost1.addHeader("Accept", "*/*");
		httpPost1.addHeader("Accept-Encoding", "gzip, deflate");
		httpPost1.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpPost1.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		httpPost1.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
		httpPost1.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
		
		String param1="jobId="+jobId+"&attachmentId="+attachmentId+"&attachmentName="+attachmentName+"&attachmentType=File&viewerType=TextFileViewer&batchSeqIds=1&component=%5Bobject%20BiListItem%5D";
		httpPost1.setEntity(new StringEntity(param1,"UTF-8"));
		
		CloseableHttpResponse response1 = httpclient.execute(httpPost1);

		String message1 = UnicodeUtil.decodeUnicode(EntityUtils.toString(response1.getEntity(),"UTF-8"));
		logger.info("查询附件结果==============================================================");
		logger.info(message1);
		logger.info("查询附件结果==============================================================");
		JSONArray arr1 = JSONObject.parseArray(message1);
		JSONObject obj1 = (JSONObject) arr1.get(1);
		String fileRef = obj1.getString("fileRef");
		
		response1.close();
		
		HttpGet httpGet = new HttpGet(URI+"/awb/dimeditor.transactionLogs.getOpenSavedialog.do?i=1&fileRef="+fileRef+"&attachmentType=File");
		
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0");
		httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		httpGet.addHeader("Accept-Encoding", "gzip,deflate");
		httpGet.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
		httpGet.addHeader("Upgrade-Insecure-Requests", "1");
		
		CloseableHttpResponse response2 = httpclient.execute(httpGet);
		try {
			String log = EntityUtils.toString(response2.getEntity(),"UTF-16LE");
			log=log.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "</br>");
			result.put("msg", log);
		} finally {
			response2.close();
		}
	}
	
}
