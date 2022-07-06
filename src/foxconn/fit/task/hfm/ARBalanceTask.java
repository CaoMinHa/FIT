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

import foxconn.fit.entity.hfm.ARBalanceInvoice;
import foxconn.fit.entity.hfm.ARBalanceSale;
import foxconn.fit.entity.hfm.ARReceive;
import foxconn.fit.service.hfm.ARBalanceInvoiceService;
import foxconn.fit.service.hfm.ARBalanceSaleService;
import foxconn.fit.service.hfm.ARReceiveService;
import foxconn.fit.util.DateUtil;

@Service
@Scope("prototype")
public class ARBalanceTask implements Runnable{
	
	public Log logger = LogFactory.getLog(this.getClass());
	
	private String id;
	private String sessionId;
	private String url;
	private String code;
	private String year;
	private String month;
	private String status;
	
	@Autowired
	private ARReceiveService receiveService;
	
	@Autowired
	private ARBalanceInvoiceService invoiceService;
	
	@Autowired
	private ARBalanceSaleService saleService;
	
	@Override
	public void run() {
		status="success";
		logger.info("开始抽取AR余额数据:[code]"+code+",[year]"+year+",[month]"+month+",[url]"+url);
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
			
			Map<String, HashMap<String,String>> messageMap=new HashMap<String, HashMap<String,String>>();
			
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
								if (parseMap(content2, omaMap)) {
									messageMap.put(omaMap.get("oma01"), omaMap);
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
									HashMap<String, String> TradeMap = messageMap.get(ombMap.get("omb01"));
									if (TradeMap!=null) {
										TradeMap.put("omb03", ombMap.get("omb03"));
										TradeMap.put("omb31", ombMap.get("omb31"));
										TradeMap.put("omb32", ombMap.get("omb32"));
										TradeMap.put("omb14", ombMap.get("omb14"));
										TradeMap.put("omb14t", ombMap.get("omb14t"));
										TradeMap.put("omb16", ombMap.get("omb16"));
										TradeMap.put("omb16t", ombMap.get("omb16t"));
										TradeMap.put("omb18", ombMap.get("omb18"));
										TradeMap.put("omb18t", ombMap.get("omb18t"));
										TradeMap.put("omb34", ombMap.get("omb34"));
										TradeMap.put("omb36", ombMap.get("omb36"));
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
			
			List<ARReceive> receiveList=new ArrayList<ARReceive>();
			List<ARBalanceInvoice> invoiceList=new ArrayList<ARBalanceInvoice>();
			List<ARBalanceSale> saleList=new ArrayList<ARBalanceSale>();
			Date monthLast = DateUtil.getMonthLast(DateUtil.parseByYyyyMM(year+month));
			for (HashMap<String, String> map : messageMap.values()) {
				ARReceive receive=new ARReceive();
				receive.setCorporationCode(code);
				receive.setYear(year);
				receive.setPeriod(month);
				receive.setDocument(map.get("oma01"));
				receive.setSummons(map.get("oma33"));
				receive.setCustomer(map.get("oma03"));
				receive.setCustomerName(map.get("oma032"));
				receive.setItemCode(map.get("oma18"));
				receive.setItemDesc(map.get("aag02"));
				receive.setCurrency(map.get("oma23"));
				receive.setSrcAmount(map.get("oma54t"));
				receive.setSrcAlamount(map.get("omb34"));
				receive.setCurrencyAmount(map.get("oma56t"));
				receive.setCurrencyAlamount(map.get("omb36"));
				String srcAmount = receive.getSrcAmount();
				String srcAlamount = receive.getSrcAlamount();
				if (StringUtils.isNotEmpty(srcAmount) && StringUtils.isNotEmpty(srcAlamount)) {
					receive.setSrcUnamount(String.valueOf(Double.parseDouble(srcAmount)-Double.parseDouble(srcAlamount)));
				}
				String currencyAmount = receive.getCurrencyAmount();
				String currencyAlamount = receive.getCurrencyAlamount();
				if (StringUtils.isNotEmpty(currencyAmount) && StringUtils.isNotEmpty(currencyAlamount)) {
					receive.setCurrencyUnamount(String.valueOf(Double.parseDouble(currencyAmount)-Double.parseDouble(currencyAlamount)));
				}
				
				receiveList.add(receive);
				
				ARBalanceInvoice invoice=new ARBalanceInvoice();
				invoice.setCorporationCode(code);
				invoice.setYear(year);
				invoice.setPeriod(month);
				invoice.setDocument(map.get("oma01"));
				invoice.setSummons(map.get("oma33"));
				invoice.setInvoice(map.get("oma10"));
				invoice.setInvoiceDate(map.get("oma09"));
				invoice.setCustomer(map.get("oma03"));
				invoice.setCustomerName(map.get("oma032"));
				invoice.setItemCode(map.get("oma18"));
				invoice.setItemDesc(map.get("aag02"));
				invoice.setCurrency(map.get("oma23"));
				invoice.setSrcAmount(map.get("oma54t"));
				invoice.setSrcTax(map.get("oma54x"));
				invoice.setSrcUntax(map.get("oma54"));
				invoice.setCurrencyAmount(map.get("oma56t"));
				invoice.setCurrencyTax(map.get("oma56x"));
				invoice.setCurrencyUntax(map.get("oma56"));
				invoice.setDocDate(map.get("oma02"));
				invoice.setDueDate(map.get("oma11"));
				invoice.setOverdueDays(map.get("agedays"));
				invoice.setDepartment(map.get("oma15"));
				invoice.setCondition(map.get("oma32"));
				
				String invoiceDate = invoice.getInvoiceDate();
				if (StringUtils.isNotEmpty(invoiceDate)) {
					Date date = DateUtil.parseByYyyy_MM_dd(invoiceDate.substring(0,10));
					long sub=monthLast.getTime()-date.getTime();
					int days=(int)(sub/(1000 * 60 * 60 * 24));
					invoice.setAging(days+"");
				}
				String dueDate = invoice.getDueDate();
				if (StringUtils.isNotEmpty(dueDate)) {
					Date date = DateUtil.parseByYyyy_MM_dd(dueDate.substring(0,10));
					long sub=monthLast.getTime()-date.getTime();
					int days=(int)(sub/(1000 * 60 * 60 * 24));
					invoice.setDueAging(days+"");
				}
				
				invoiceList.add(invoice);
				
				ARBalanceSale sale=new ARBalanceSale();
				sale.setCorporationCode(code);
				sale.setYear(year);
				sale.setPeriod(month);
				sale.setDocument(map.get("oma01"));
				sale.setSummons(map.get("oma33"));
				sale.setSale(map.get("omb31"));
				sale.setCategory(map.get("omb32"));
				sale.setCustomer(map.get("oma03"));
				sale.setCustomerName(map.get("oma032"));
				sale.setItemCode(map.get("oma18"));
				sale.setItemDesc(map.get("aag02"));
				sale.setCurrency(map.get("oma23"));
				sale.setSrcAmount(map.get("oma54t"));
				sale.setCurrencyAmount(map.get("oma56t"));
				sale.setDepartment(map.get("oma15"));
				sale.setCondition(map.get("oma32"));
				
				saleList.add(sale);
			}
			
			receiveService.saveBatch(receiveList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			invoiceService.saveBatch(invoiceList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			saleService.saveBatch(saleList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			
			logger.info("抽取AR余额数据成功:[code]"+code+",[year]"+year+",[month]"+month);
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (Exception e1) {
			}
			status="fail";
			logger.error("AR余额抽取失败:[code]"+code+",[year]"+year+",[month]"+month, e);
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
	
	private static boolean parseMap(List content, HashMap<String,String> map) {
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
