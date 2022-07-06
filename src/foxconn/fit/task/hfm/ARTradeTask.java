package foxconn.fit.task.hfm;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import foxconn.fit.entity.hfm.ARTradeInvoice;
import foxconn.fit.entity.hfm.ARTradeSale;
import foxconn.fit.service.hfm.ARTradeInvoiceService;
import foxconn.fit.service.hfm.ARTradeSaleService;
import foxconn.fit.util.DateUtil;

@Service
@Scope("prototype")
public class ARTradeTask implements Runnable{
	
	public Log logger = LogFactory.getLog(this.getClass());
	
	private String id;
	private String sessionId;
	private String url;
	private String code;
	private String year;
	private String month;
	private String status;
	
	@Autowired
	private ARTradeInvoiceService invoiceService;
	
	@Autowired
	private ARTradeSaleService saleService;
	
	@Override
	public void run() {
		status="success";
		logger.info("开始抽取AR交易额数据:[code]"+code+",[year]"+year+",[month]"+month+",[url]"+url);
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
			Document document = saxReader.read(new ByteArrayInputStream(xmlString.replaceAll("&", "&amp;").getBytes("UTF-8")));
			Element root = document.getRootElement();
			List<DefaultElement> elements = root.elements();
			DefaultElement element = elements.get(1);
			List content = element.content();
			
			Map<String, HashMap<String,String>> tradeMap=new HashMap<String, HashMap<String,String>>();
			
			for (Object object : content) {
				if (object instanceof DefaultElement) {
					DefaultElement NewDataSet=(DefaultElement)object;
					List content1 = NewDataSet.content();
					for (Object object1 : content1) {
						if (object1 instanceof DefaultElement) {
							DefaultElement ds=(DefaultElement)object1;
							List content2 = ds.content();
							switch (ds.getName()) {
							case "dtoma":
								HashMap<String, String> omaMap=new HashMap<String, String>();
								if (parseTrade(content2, omaMap)) {
									tradeMap.put(omaMap.get("oma01"), omaMap);
								}
								break;
							case "dtomb":
								Map<String,String> ombMap=new HashMap<String, String>();
								
								boolean valid=false;
								for (Object object2 : content2) {
									if (object2 instanceof DefaultElement) {
										valid=true;
										DefaultElement ele=(DefaultElement)object2;
										String name = ele.getName();
										String value = ele.getTextTrim().replaceAll("&amp;","&");
										ombMap.put(name, value);
									}
								}
								
								if (valid) {
									HashMap<String, String> TradeMap = tradeMap.get(ombMap.get("omb01"));
									if (TradeMap!=null) {
										TradeMap.put("omb03", ombMap.get("omb03"));
										TradeMap.put("omb11", ombMap.get("omb11"));
										TradeMap.put("omb14", ombMap.get("omb14"));
										TradeMap.put("omb16", ombMap.get("omb16"));
									}
								}
								break;
							default:
								break;
							}
						}
					}
				}
			}
			
			List<ARTradeInvoice> invoiceList=new ArrayList<ARTradeInvoice>();
			List<ARTradeSale> saleList=new ArrayList<ARTradeSale>();
			for (HashMap<String, String> map : tradeMap.values()) {
				ARTradeInvoice invoice=new ARTradeInvoice();
				invoice.setCorporationCode(code);
				invoice.setYear(year);
				invoice.setPeriod(month);
				invoice.setDocument(map.get("oma01"));
				invoice.setSummons(map.get("oma33"));
				invoice.setInvoice(map.get("oma10"));
				invoice.setCustomer(map.get("oma03"));
				invoice.setCustomerName(map.get("oma032"));
				invoice.setBorrowItemCode(map.get("ool11"));
				invoice.setBorrowItemDesc(map.get("ool02"));
				invoice.setItemCode(map.get("ool41"));
				invoice.setItemDesc(map.get("aag02"));
				invoice.setCurrency(map.get("oma23"));
				invoice.setTaxSrcAmount(map.get("oma54t"));
				invoice.setUntaxSrcAmount(map.get("omb14"));
				invoice.setTaxCurrencyAmount(map.get("oma59t"));
				invoice.setTaxCamount(map.get("oma59x"));
				invoice.setUntaxCurrencyAmount(map.get("oma59"));
				invoice.setDepartment(map.get("oma15"));
				
				invoiceList.add(invoice);
				
				ARTradeSale sale=new ARTradeSale();
				sale.setCorporationCode(code);
				sale.setYear(year);
				sale.setPeriod(month);
				sale.setDocument(map.get("oma01"));
				sale.setSummons(map.get("oma33"));
				sale.setCategory(map.get("omb03"));
				sale.setCustomer(map.get("oma03"));
				sale.setCustomerName(map.get("oma032"));
				sale.setBorrowItemCode(map.get("ool11"));
				sale.setBorrowItemDesc(map.get("ool02"));
				sale.setItemCode(map.get("ool41"));
				sale.setItemDesc(map.get("aag02"));
				sale.setCurrency(map.get("oma23"));
				sale.setUntaxSrcAmount(map.get("omb14"));
				sale.setUntaxCurrencyAmount(map.get("omb16"));
				sale.setDepartment(map.get("oma15"));
				
				saleList.add(sale);
			}
			
			invoiceService.saveBatch(invoiceList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			saleService.saveBatch(saleList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			
			logger.info("抽取AR交易额数据成功:[code]"+code+",[year]"+year+",[month]"+month);
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (Exception e1) {
			}
			status="fail";
			logger.error("AR交易额抽取失败:[code]"+code+",[year]"+year+",[month]"+month, e);
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
	
	private static boolean parseTrade(List content, HashMap<String,String> map) {
		boolean valid=false;
		for (Object object : content) {
			if (object instanceof DefaultElement) {
				valid=true;
				DefaultElement ele=(DefaultElement)object;
				String name = ele.getName();
				String value = ele.getTextTrim().replaceAll("&amp;","&");
				map.put(name, value);
			}
		}
		
		return valid;
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
