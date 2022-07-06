package foxconn.fit.task.bi;

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

import foxconn.fit.entity.bi.RevenueDetailActualNumber;
import foxconn.fit.service.bi.RevenueDetailActualNumberService;
import foxconn.fit.util.DateUtil;

@Service
@Scope("prototype")
public class RevenueDetailActualNumberTask implements Runnable{
	
	public Log logger = LogFactory.getLog(this.getClass());
	
	private String id;
	private String sessionId;
	private String url;
	private String corporationCode;
	private String erpCode;
	private String year;
	private String month;
	private String status;
	private String version;
	
	@Autowired
	private RevenueDetailActualNumberService revenueDetailActualNumberService;
	
	@Override
	public void run() {
		status="success";
		logger.info("开始抽取營收明細實際數数据:[corporationCode]"+corporationCode+",[year]"+year+",[month]"+month);
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);

			httpPost.addHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

			String param = "gbcode="+erpCode+"&year="+year+"&month="+month;
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
			
			List<RevenueDetailActualNumber> list=new ArrayList<RevenueDetailActualNumber>();
			
			for (Object object : content) {
				if (object instanceof DefaultElement) {
					DefaultElement NewDataSet=(DefaultElement)object;
					List content1 = NewDataSet.content();
					for (Object object1 : content1) {
						if (object1 instanceof DefaultElement) {
							DefaultElement ds=(DefaultElement)object1;
							List content2 = ds.content();
							
							RevenueDetailActualNumber revenue=new RevenueDetailActualNumber();
							revenue.setVersion(this.version);
							
							for (Object object2 : content2) {
								if (object2 instanceof DefaultElement) {
									DefaultElement text=(DefaultElement)object2;
									String name = text.getName();
									String value = text.getTextTrim();
									switch (name) {
									case "entity":
										revenue.setCorporationCode(corporationCode);
										break;
									case "year":
										revenue.setYear(value);
										break;
									case "period":
										revenue.setPeriod(value);
										break;
									case "oga03":
										revenue.setCustomerCode(value);
										break;
									case "occ02":
										revenue.setCustomerName(value);
										break;
									case "oga14":
										revenue.setDepartment(value);
										break;
									case "oma13":
										revenue.setCategory(value);
										break;
									case "ogb03":
										revenue.setInvoiceItem(value);
										break;
									case "oga01":
										revenue.setInvoiceNo(value);
										break;
									case "oma02":
										revenue.setInvoiceSignDate(value);
										break;
									case "oga011":
										revenue.setSaleNo(value);
										break;
									case "oga02":
										revenue.setSaleDate(value);
										break;
									case "ogb09":
										revenue.setStoreNo(value);
										break;
									case "ogb04":
										revenue.setProductNo(value);
										break;
									case "ogb11":
										revenue.setCustomerProductNo(value);
										break;
									case "ogb12":
										revenue.setQuantity(value);
										break;
									case "ogb13":
										revenue.setPrice(value);
										break;
									case "oga23":
										revenue.setCurrency(value);
										break;
									case "oga24":
										revenue.setRate(value);
										break;
									case "ogb14":
										revenue.setSourceUntaxAmount(value);
										break;
									case "ogb14t":
										revenue.setCurrencyUntaxAmount(value);
										break;
									case "oga15":
										revenue.setProductionUnit(value);
										break;
									case "oga19":
										revenue.setCd(value);
										break;
									default:
										break;
									}
										
								}
							}
							
							list.add(revenue);
						}
					}
				}
			}
			
			revenueDetailActualNumberService.saveExtractData(list, "where year='"+year+"' and period='"+month+"' and version='"+version+"' and corporation_code='"+corporationCode+"'");
			
			logger.info("抽取營收明細實際數数据成功:[corporationCode]"+corporationCode+",[year]"+year+",[month]"+month);
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (Exception e1) {
			}
			status="fail";
			logger.error("營收明細實際數抽取失败:[corporationCode]"+corporationCode+",[year]"+year+",[month]"+month, e);
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

	public void setParam(String id,String sessionId,String url,String corporationCode,String erpCode,String year,String month,String version) {
		this.id = id;
		this.sessionId=sessionId;
		this.url=url;
		this.corporationCode=corporationCode;
		this.erpCode=erpCode;
		this.year=year;
		this.month=month;
		this.version=version;
	}

}
