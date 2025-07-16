package foxconn.fit.task.bi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ScriptSessionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import foxconn.fit.entity.budget.BudgetProductBasicData;

import foxconn.fit.service.budget.BudgetProductBasicDataService;
import foxconn.fit.util.DateUtil;

/**
 * @author CXJ
 * 料號對接
 */
@Service
@Scope("prototype")

public class BudgetProductBasicDataTask implements Runnable{
	
	public Log logger = LogFactory.getLog(this.getClass());
	
	private String id;
	private String sessionId;
	private String url;
	private String status;
	private String date1;
	@Autowired
	private BudgetProductBasicDataService BudgetproductbasicdataService;
	
	@Override
	public void run() {
		status="FAIL";
		Date date=new Date();
		date1=DateUtil.formatByHHmmss(date);
		
		logger.info("开始抽取数据:[date]"+date1+",[url]"+url);
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);

			/*httpPost.addHeader("Content-Type","application/json");*/
            HttpResponse response = httpclient.execute(httpPost);
			List<BudgetProductBasicData> list=new ArrayList<BudgetProductBasicData>();
			logger.info("jason"+response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
            	HttpEntity entity = response.getEntity();
    			String data = EntityUtils.toString(entity);
    			JSONObject jason = JSON.parseObject(data); 
    			logger.info("jason"+jason.getBoolean("success"));	
    			if(jason.getBoolean("success")) {
    			  JSONArray jasonarray=jason.getJSONArray("data");
    			  for(int i=1;i<jasonarray.size();i++) {
    				JSONObject object=jasonarray.getJSONObject(i);
    				BudgetProductBasicData Budgetproductbasicdata=new BudgetProductBasicData();
    				Budgetproductbasicdata.setCreateDate(DateUtil.formatByYyyyMMddHHmmss(date));
    				Budgetproductbasicdata.setproductNO(object.getString("partNo_col"));
    				Budgetproductbasicdata.setId(Integer.toString(i));
    				list.add(Budgetproductbasicdata);
    			 }
    			 
    			  
    			}
            }	
            if (!list.isEmpty()) {					
            	BudgetproductbasicdataService.saveBatch(list);
    			status="SUCCESS";
    			logger.info("抽取成功:[數量]"+list.size());	
			} else {
				status="FAIL";
    			logger.info("無數據失敗:[date]"+date1);	
			}
			
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (Exception e1) {
			}
			status="fail";
			logger.error("抽取失败:[date]"+date1, e);
		}
		
		/*Browser.withAllSessionsFiltered(new ScriptSessionFilter() {

			public boolean match(ScriptSession session) {

				if (session.getAttribute("sessionId") == null) {
					return false;
				} else {
					return (session.getAttribute("sessionId")).equals(sessionId);
				}
			}

		}, new Runnable() {

			private ScriptBuffer script = new ScriptBuffer();

			public void run() {
				String time = DateUtil.formatByHHmmss(new Date());
				script.appendCall("updateTask", id,status,time);

				Collection<ScriptSession> sessions = Browser
						.getTargetSessions();

				for (ScriptSession scriptSession : sessions) {
					scriptSession.addScript(script);
				}
			}
		});*/
	}
/*
	public void setParam(String id,String sessionId,String url) {
		this.id = id;
		this.sessionId=sessionId;
		this.url=url;
	}*/
	public void setParam(String id,String url) {
		this.id = id;
		this.url=url;
	}

}

