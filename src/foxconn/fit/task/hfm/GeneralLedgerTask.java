package foxconn.fit.task.hfm;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
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
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import foxconn.fit.entity.hfm.GeneralLedger;
import foxconn.fit.service.hfm.GeneralLedgerService;
import foxconn.fit.util.DateUtil;

@Service
@Scope("prototype")
public class GeneralLedgerTask implements Runnable{
	
	public Log logger = LogFactory.getLog(this.getClass());
	
	private String id;
	private String sessionId;
	private String url;
	private String code;
	private String year;
	private String month;
	private String status;
	
	@Autowired
	private GeneralLedgerService generalLedgerService;
	
	@Override
	public void run() {
		status="success";
		logger.info("开始抽取总账数据:[code]"+code+",[year]"+year+",[month]"+month+",[url]"+url);
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);

			httpPost.addHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

			String param = "gbcode="+code+"&year="+year+"&month="+month;
			httpPost.setEntity(new StringEntity(param, HTTP.UTF_8));

			CloseableHttpResponse response = httpclient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			String xmlString = EntityUtils.toString(entity, HTTP.UTF_8);
			if (StringUtils.isNotEmpty(xmlString) && xmlString.length()<100) {
				logger.info("xmlString:"+xmlString);
			}

			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
			Element root = document.getRootElement();
			List<DefaultElement> elements = root.elements();
			DefaultElement element = elements.get(1);
			List content = element.content();
			
			List<GeneralLedger> list=new ArrayList<GeneralLedger>();
			
			for (Object object : content) {
				if (object instanceof DefaultElement) {
					DefaultElement NewDataSet=(DefaultElement)object;
					List content1 = NewDataSet.content();
					for (Object object1 : content1) {
						if (object1 instanceof DefaultElement) {
							DefaultElement ds=(DefaultElement)object1;
							List content2 = ds.content();
							
							GeneralLedger generalLedger=new GeneralLedger();
							generalLedger.setCorporationCode(code);
							generalLedger.setYear(year);
							generalLedger.setPeriod(month);
							
							List<String> valueList=new ArrayList<String>();
							for (Object object2 : content2) {
								if (object2 instanceof DefaultElement) {
									DefaultElement text=(DefaultElement)object2;
									String name = text.getName();
									String value = text.getTextTrim();
									valueList.add(text.getTextTrim());
									switch (name) {
									case "aag01":
										generalLedger.setItemCode(value);
										break;
									case "aag02":
										generalLedger.setItemDesc(value);
										break;
									case "aag04":
										generalLedger.setAssetsState(value);
										break;
									case "aag06":
										generalLedger.setBalanceState(value);
										break;
									case "bamt":
										generalLedger.setBeginBalance(value);
										break;
									case "damt":
										generalLedger.setCurrDebitBalance(value);
										break;
									case "camt":
										generalLedger.setCurrCreditBalance(value);
										break;
									case "eamt":
										generalLedger.setEndBalance(value);
										break;
									default:
										break;
									}
										
								}
							}
							
							list.add(generalLedger);
						}
					}
				}
			}
			List<GeneralLedger> newList=new ArrayList<GeneralLedger>();
			for (GeneralLedger generalLedger : list) {
				String beginBalance = generalLedger.getBeginBalance();
				String currDebitBalance = generalLedger.getCurrDebitBalance();
				String currCreditBalance = generalLedger.getCurrCreditBalance();
				String endBalance = generalLedger.getEndBalance();
				if (StringUtils.isNotEmpty(beginBalance) && Double.parseDouble(beginBalance)==0 
						&& StringUtils.isNotEmpty(currDebitBalance) && Double.parseDouble(currDebitBalance)==0
						&& StringUtils.isNotEmpty(currCreditBalance) && Double.parseDouble(currCreditBalance)==0
						&& StringUtils.isNotEmpty(endBalance) && Double.parseDouble(endBalance)==0) {
				}else{
					newList.add(generalLedger);
				}
			}
			generalLedgerService.saveBatch(newList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			
			logger.info("抽取总账数据成功:[code]"+code+",[year]"+year+",[month]"+month);
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (Exception e1) {
			}
			status="fail";
			logger.error("总账抽取失败:[code]"+code+",[year]"+year+",[month]"+month, e);
		}
		
		Browser.withAllSessionsFiltered(new ScriptSessionFilter() {

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
		});
	}

	public void setParam(String id,String sessionId,String url,String code,String year,String month) {
		this.id = id;
		this.sessionId=sessionId;
		this.url=url;
		this.code=code;
		this.year=year;
		this.month=month;
	}

}
