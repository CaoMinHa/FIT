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

import foxconn.fit.entity.hfm.APBalanceInvoice;
import foxconn.fit.entity.hfm.APTradeInvoice;
import foxconn.fit.entity.hfm.APTradeStorage;
import foxconn.fit.service.hfm.APTradeInvoiceService;
import foxconn.fit.service.hfm.APTradeStorageService;
import foxconn.fit.util.DateUtil;

@Service
@Scope("prototype")
public class APTradeTask implements Runnable{
	
	public Log logger = LogFactory.getLog(this.getClass());
	
	private String id;
	private String sessionId;
	private String url;
	private String code;
	private String year;
	private String month;
	private String status;
	
	@Autowired
	private APTradeInvoiceService invoiceService;
	
	@Autowired
	private APTradeStorageService storageService;
	
	@Override
	public void run() {
		status="success";
		logger.info("开始抽取AP交易额数据:[code]"+code+",[year]"+year+",[month]"+month+",[url]"+url);
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
							case "dtapa":
								HashMap<String, String> apaMap=new HashMap<String, String>();
								if (parseAPTrade(content2, apaMap)) {
									tradeMap.put(apaMap.get("apa01"), apaMap);
								}
								break;
							case "dtapk":
								Map<String,String> apkMap=new HashMap<String, String>();
								
								boolean valid=false;
								for (Object object2 : content2) {
									if (object2 instanceof DefaultElement) {
										valid=true;
										DefaultElement ele=(DefaultElement)object2;
										String name = ele.getName();
										String value = ele.getTextTrim().replaceAll("&amp;","&");
										apkMap.put(name, value);
									}
								}
								
								if (valid) {
									HashMap<String, String> apaMap1 = tradeMap.get(apkMap.get("apk01"));
									if (apaMap1!=null) {
										apaMap1.put("apk03", apkMap.get("apk03"));
										apaMap1.put("apk05", apkMap.get("apk05"));
										apaMap1.put("apk08", apkMap.get("apk08"));
										apaMap1.put("apk07", apkMap.get("apk07"));
										apaMap1.put("apk06", apkMap.get("apk06"));
									}
								}
								
								break;
							case "dtapb":
								Map<String,String> apbMap=new HashMap<String, String>();
								
								boolean isValid=false;
								for (Object object2 : content2) {
									if (object2 instanceof DefaultElement) {
										isValid=true;
										DefaultElement ele=(DefaultElement)object2;
										String name = ele.getName();
										String value = ele.getTextTrim().replaceAll("&amp;","&");
										apbMap.put(name, value);
									}
								}
								
								if (isValid) {
									HashMap<String, String> apaMap1 = tradeMap.get(apbMap.get("apb01"));
									if (apaMap1!=null) {
										apaMap1.put("apb02", apbMap.get("apb02"));
										apaMap1.put("apb21", apbMap.get("apb21"));
										apaMap1.put("apb26", apbMap.get("apb26"));
										apaMap1.put("apb24", apbMap.get("apb24"));
										apaMap1.put("apb10", apbMap.get("apb10"));
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
			
			List<APTradeInvoice> invoiceList=new ArrayList<APTradeInvoice>();
			List<APTradeStorage> storageList=new ArrayList<APTradeStorage>();
			for (HashMap<String, String> map : tradeMap.values()) {
				APTradeInvoice invoice=new APTradeInvoice();
				invoice.setCorporationCode(code);
				invoice.setYear(year);
				invoice.setPeriod(month);
				invoice.setDocument(map.get("apa01"));
				invoice.setSummons(map.get("apa44"));
				invoice.setInvoice(map.get("apk03"));
				invoice.setSupplier(map.get("apa06"));
				invoice.setSupplierName(map.get("apa07"));
				invoice.setBorrowItemCode(map.get("apa51"));
				invoice.setBorrowItemDesc(map.get("daag02"));
				invoice.setItemCode(map.get("apa54"));
				invoice.setItemDesc(map.get("caag02"));
				invoice.setCurrency(map.get("apa13"));
				invoice.setTaxSrcAmount(map.get("apa34f"));
				invoice.setTaxSamount(map.get("apa32f"));
				invoice.setUntaxSrcAmount(map.get("apa31f"));
				invoice.setTaxCurrencyAmount(map.get("apa34"));
				invoice.setTaxCamount(map.get("apa32"));
				invoice.setUntaxCurrencyAmount(map.get("apa31"));
				invoice.setDepartment(map.get("apb26"));
				invoice.setSummary(map.get("apa25"));
				
				invoiceList.add(invoice);
				
				APTradeStorage storage=new APTradeStorage();
				storage.setCorporationCode(code);
				storage.setYear(year);
				storage.setPeriod(month);
				storage.setDocument(map.get("apa01"));
				storage.setSummons(map.get("apa44"));
				storage.setNumber(map.get("apb21"));
				storage.setCategory(map.get("apb02"));
				storage.setSupplier(map.get("apa06"));
				storage.setSupplierName(map.get("apa07"));
				storage.setBorrowItemCode(map.get("apa51"));
				storage.setBorrowItemDesc(map.get("daag02"));
				storage.setItemCode(map.get("apa54"));
				storage.setItemDesc(map.get("caag02"));
				storage.setCurrency(map.get("apa13"));
				storage.setSrcUntaxAmount(map.get("apb24"));
				storage.setCurrencyUntaxAmount(map.get("apb10"));
				storage.setDepartment(map.get("apb26"));
				storage.setSummary(map.get("apa25"));
				
				storageList.add(storage);
			}
			
			invoiceService.saveBatch(invoiceList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			storageService.saveBatch(storageList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			
			logger.info("抽取AP交易额数据成功:[code]"+code+",[year]"+year+",[month]"+month);
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (Exception e1) {
			}
			status="fail";
			logger.error("AP交易额抽取失败:[code]"+code+",[year]"+year+",[month]"+month, e);
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

	private static boolean parseAPBalanceInvoice(List content,APBalanceInvoice invoice) {
		boolean valid=false;
		for (Object object : content) {
			if (object instanceof DefaultElement) {
				valid=true;
				DefaultElement ele=(DefaultElement)object;
				String name = ele.getName();
				String value = ele.getTextTrim().replaceAll("&amp;","&");
				switch (name) {
				case "apk01":
					invoice.setDocument(value);
					break;
				case "apk03":
					invoice.setInvoice(value);
					break;
				case "apk06":
					invoice.setSrcAmount(value);
					break;
				case "apk07":
					invoice.setSrcTax(value);
					break;
				case "apk08":
					invoice.setSrcUntax(value);
					break;
				default:
					break;
				}
			}
		}
		
		return valid;
	}

	private static boolean parseAPTrade(List content, HashMap<String,String> map) {
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
