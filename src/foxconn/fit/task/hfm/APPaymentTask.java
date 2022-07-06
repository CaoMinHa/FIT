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
import foxconn.fit.entity.hfm.APBalanceStorage;
import foxconn.fit.entity.hfm.APPayment;
import foxconn.fit.service.hfm.APBalanceInvoiceService;
import foxconn.fit.service.hfm.APBalanceStorageService;
import foxconn.fit.service.hfm.APPaymentService;
import foxconn.fit.util.DateUtil;

@Service
@Scope("prototype")
public class APPaymentTask implements Runnable{
	
	public Log logger = LogFactory.getLog(this.getClass());
	
	private String id;
	private String sessionId;
	private String url;
	private String code;
	private String year;
	private String month;
	private String status;
	
	@Autowired
	private APPaymentService apPaymentService;
	
	@Autowired
	private APBalanceInvoiceService apBalanceInvoiceService;
	
	@Autowired
	private APBalanceStorageService apBalanceStorageService;
	
	@Override
	public void run() {
		status="success";
		logger.info("开始抽取AP余额数据:[code]"+code+",[year]"+year+",[month]"+month+",[url]"+url);
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
			
			Map<String, HashMap<String,String>> paymentMap=new HashMap<String, HashMap<String,String>>();
			List<APPayment> paymentList=new ArrayList<APPayment>();
			List<APBalanceInvoice> invoiceList=new ArrayList<APBalanceInvoice>();
			List<APBalanceStorage> storageList=new ArrayList<APBalanceStorage>();
			
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
								HashMap<String, String> map=new HashMap<String, String>();
								if (parseAPPayment(content2, map)) {
									paymentMap.put(map.get("apa01"), map);
									
									APPayment payment=new APPayment();
									payment.setCorporationCode(code);
									payment.setYear(year);
									payment.setPeriod(month);
									payment.setDocument(map.get("apa01"));
									payment.setSummons(map.get("apa44"));
									payment.setSupplier(map.get("apa06"));
									payment.setSupplierName(map.get("apa07"));
									payment.setBorrowItemCode(map.get("apa51"));
									payment.setBorrowItemDesc(map.get("daag02"));
									payment.setItemCode(map.get("apa54"));
									payment.setItemDesc(map.get("caag02"));
									payment.setCurrency(map.get("apa13"));
									payment.setSrcAmount(map.get("apa34f"));
									payment.setSrcAlamount(map.get("apa35f"));
									payment.setSrcUnamount(map.get("oamt"));
									payment.setCurrencyAmount(map.get("apa34"));
									payment.setCurrencyAlamount(map.get("apa35"));
									payment.setCurrencyUnamount(map.get("amt"));
									payment.setCondition(map.get("apa11"));
									payment.setSummary(map.get("apa25"));
									payment.setNote(map.get("apa251"));
									
									paymentList.add(payment);
									
									APBalanceStorage storage=new APBalanceStorage();
									storage.setCorporationCode(code);
									storage.setYear(year);
									storage.setPeriod(month);
									storage.setDocument(map.get("apa01"));
									storage.setSummons(map.get("apa44"));
									storage.setSupplier(map.get("apa06"));
									storage.setSupplierName(map.get("apa07"));
									storage.setBorrowItemCode(map.get("apa51"));
									storage.setBorrowItemDesc(map.get("daag02"));
									storage.setItemCode(map.get("apa54"));
									storage.setItemDesc(map.get("caag02"));
									storage.setCurrency(map.get("apa13"));
									storage.setSrcUntaxAmount(map.get("apa31f"));
									storage.setCurrencyUntaxAmount(map.get("apa31"));
									storage.setDepartment(map.get("apa22"));
									storage.setSummary(map.get("apa25"));
									
									storageList.add(storage);
								}
								break;
							case "dtapk":
								APBalanceInvoice invoice=new APBalanceInvoice();
								if (parseAPBalanceInvoice(content2, invoice)) {
									invoiceList.add(invoice);
								}
								break;
							default:
								break;
							}
						}
					}
				}
			}
			
			if (!invoiceList.isEmpty()) {
				for (APBalanceInvoice invoice : invoiceList) {
					HashMap<String, String> map = paymentMap.get(invoice.getDocument());
					if (map!=null) {
						invoice.setCorporationCode(code);
						invoice.setYear(year);
						invoice.setPeriod(month);
						invoice.setSummons(map.get("apa44"));
						invoice.setSupplier(map.get("apa06"));
						invoice.setSupplierName(map.get("apa07"));
						invoice.setBorrowItemCode(map.get("apa51"));
						invoice.setBorrowItemDesc(map.get("daag02"));
						invoice.setItemCode(map.get("apa54"));
						invoice.setItemDesc(map.get("caag02"));
						invoice.setCurrency(map.get("apa13"));
						invoice.setCurrencyAmount(map.get("apa34"));
						invoice.setCurrencyTax(map.get("apa32"));
						invoice.setCurrencyUntax(map.get("apa31"));
						invoice.setDocDate(map.get("apa02"));
						invoice.setDueDate(map.get("apa64"));
						invoice.setAging(map.get("agedays"));
						invoice.setDepartment(map.get("apa22"));
						invoice.setCondition(map.get("apa11"));
						invoice.setSummary(map.get("apa25"));
						invoice.setNote(map.get("apa251"));
					}
				}
			}
			
			apPaymentService.saveBatch(paymentList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			apBalanceInvoiceService.saveBatch(invoiceList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			apBalanceStorageService.saveBatch(storageList, "where year='"+year+"' and period='"+month+"' and corporation_code='"+code+"'");
			
			logger.info("抽取AP余额数据成功:[code]"+code+",[year]"+year+",[month]"+month);
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (Exception e1) {
			}
			status="fail";
			logger.error("AP余额抽取失败:[code]"+code+",[year]"+year+",[month]"+month, e);
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

	private static boolean parseAPPayment(List content, HashMap<String,String> map) {
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
