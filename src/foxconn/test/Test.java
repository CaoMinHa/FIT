package foxconn.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

import foxconn.fit.entity.hfm.APBalanceInvoice;
import foxconn.fit.entity.hfm.APBalanceStorage;
import foxconn.fit.entity.hfm.APPayment;

public class Test {


	public static void main(String[] args) throws Exception {
//		CloseableHttpClient httpclient = HttpClients.createDefault();
//		HttpPost httpPost = new HttpPost("http://10.196.7.96:7019/api/MyData/Send");
//		Map<String,String> map=new HashMap<String, String>();
//		map.put("System", "AC001");
//		map.put("Dept", "FIT");
//		map.put("Light", "2");
//		map.put("Subject", "Alarm Subject");
//		map.put("Body", "Alarm Subject");
//		map.put("ReportTime", "2018.11");
//		map.put("ReportValue", "123");
//		map.put("ReportURL", "http://10.98.5.23:8080/fit/unauth/alarmCallback?id=805ACA048AA25133E0531505620A32C1");
//		//"System":"AC001","Dept":"FIT","Light":"2","Subject":"Alarm Subject","Body":"Alarm Subject123","ReportTime":"201811","ReportValue":"3","ReportURL":"1213123"
//		String param=JSONObject.fromObject(map).toString();
//		System.out.println(param);
//		httpPost.setEntity(new StringEntity(param,"UTF-8"));
//		httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//		httpPost.addHeader("Accept-Encoding", "gzip, deflate");
//		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
//		httpPost.addHeader("Content-Type", "application/json");
//
//		CloseableHttpResponse response = httpclient.execute(httpPost);
//
//		HttpEntity entity = response.getEntity();
//		String result = EntityUtils.toString(entity,"UTF-8");
//		System.out.println("结果==============================================================");
//		System.out.println(result);
//		com.alibaba.fastjson.JSONObject parseObject = com.alibaba.fastjson.JSONObject.parseObject(result);
//		String IsOK = parseObject.getString("IsOK");
//		System.out.println(IsOK);
////		APExtracted();
////		extracted();
////		connectSQLServer();
		List<List<String>> lists=new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			List<String> a=new ArrayList<>();
			a.add("1");
			a.add("12");
			lists.add(a);
		}
		List<String> b=new ArrayList<>();
		b.add("c");
		b.add("12");
		lists.add(b);

		List<List<String>> lists1 = ifRepeat(lists);
	}
	
	private static void APExtracted() throws Exception {
		File file = new File("C:\\Users\\liangchen\\Desktop\\workspace\\fit\\src\\foxconn\\test\\ap.xml");
		FileReader fileReader=new FileReader(file);
		BufferedReader br=new BufferedReader(fileReader);
		StringBuffer message = new StringBuffer();  
		String line = null;  
		while((line = br.readLine()) != null) {  
			message.append(line.replaceAll("&", "&amp;")).append("\n");
		}  
		String xmlString=message.toString();
		
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
		Element root = document.getRootElement();
		List<DefaultElement> elements = root.elements();
		DefaultElement element = elements.get(1);
		List content = element.content();
		
		Map<String, APPayment> paymentMap=new HashMap<String, APPayment>();
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
							APPayment payment=new APPayment();
							if (parseAPPayment(content2, payment)) {
								paymentMap.put(payment.getDocument(), payment);
							}
							break;
						case "dtapk":
							APBalanceInvoice invoice=new APBalanceInvoice();
							if (parseAPBalanceInvoice(content2, invoice)) {
								invoiceList.add(invoice);
							}
							break;
						case "dtapb":
							APBalanceStorage storage=new APBalanceStorage();
							if (parseAPBalanceStorage(content2, storage)) {
								storageList.add(storage);
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
				APPayment payment = paymentMap.get(invoice.getDocument());
				if (payment!=null) {
					invoice.setCorporationCode(payment.getCorporationCode());
					invoice.setYear(payment.getYear());
					invoice.setPeriod(payment.getPeriod());
					invoice.setSummons(payment.getSummons());
					invoice.setSupplier(payment.getSupplier());
					invoice.setSupplierName(payment.getSupplierName());
					invoice.setItemCode(payment.getItemCode());
					invoice.setItemDesc(payment.getItemDesc());
					invoice.setCurrency(payment.getCurrency());
					invoice.setSrcAmount(payment.getSrcAmount());
					invoice.setCurrencyAmount(payment.getCurrencyAmount());
					invoice.setCondition(payment.getCondition());
					invoice.setSummary(payment.getSummary());
					invoice.setNote(payment.getNote());
				}
			}
		}
		
		if (!storageList.isEmpty()) {
			for (APBalanceStorage storage : storageList) {
				APPayment payment = paymentMap.get(storage.getDocument());
				if (payment!=null) {
					storage.setCorporationCode(payment.getCorporationCode());
					storage.setYear(payment.getYear());
					storage.setPeriod(payment.getPeriod());
					storage.setSummons(payment.getSummons());
					storage.setSupplier(payment.getSupplier());
					storage.setSupplierName(payment.getSupplierName());
					storage.setItemCode(payment.getItemCode());
					storage.setItemDesc(payment.getItemDesc());
					storage.setCurrency(payment.getCurrency());
				}
			}
		}
	}
	
	private static boolean parseAPBalanceStorage(List content,APBalanceStorage storage) {
		boolean valid=false;
		for (Object object : content) {
			if (object instanceof DefaultElement) {
				valid=true;
				DefaultElement ele=(DefaultElement)object;
				String name = ele.getName();
				String value = ele.getTextTrim().replaceAll("&amp;","&");
				switch (name) {
				case "apb01":
					storage.setDocument(value);
					break;
				case "apb21":
					storage.setNumber(value);
					break;
				case "apb02":
					storage.setCategory(value);
					break;
				case "apb24":
					storage.setSrcUntaxAmount(value);
					break;
				case "apb10":
					storage.setCurrencyUntaxAmount(value);
					break;
				case "apb26":
					storage.setDepartment(value);
					break;
				default:
					break;
				}
			}
		}
		return valid;
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
				case "apk07":
					invoice.setSrcTax(value);
					break;
				case "apk08":
					invoice.setSrcUntax(value);
					break;
				case "apk05":
					invoice.setDocDate(value);
					break;
				default:
					break;
				}
			}
		}
		
		return valid;
	}

	private static boolean parseAPPayment(List content, APPayment payment) {
		boolean valid=false;
		for (Object object : content) {
			if (object instanceof DefaultElement) {
				valid=true;
				DefaultElement ele=(DefaultElement)object;
				String name = ele.getName();
				String value = ele.getTextTrim().replaceAll("&amp;","&");
				switch (name) {
				case "entity":
					payment.setCorporationCode(value);
					break;
				case "year":
					payment.setYear(value);
					break;
				case "period":
					payment.setPeriod(value);
					break;
				case "apa01":
					payment.setDocument(value);
					break;
				case "apa44":
					payment.setSummons(value);
					break;
				case "apa06":
					payment.setSupplier(value);
					break;
				case "apa07":
					payment.setSupplierName(value);
					break;
				case "apa54":
					payment.setItemCode(value);
					break;
				case "aag02":
					payment.setItemDesc(value);
					break;
				case "apa11":
					payment.setCondition(value);
					break;
				case "apa13":
					payment.setCurrency(value);
					break;
				case "ocp":
					payment.setSrcAmount(value);
					break;
				case "apa35f":
					payment.setSrcAlamount(value);
					break;
				case "oamt":
					payment.setSrcUnamount(value);
					break;
				case "lcp":
					payment.setCurrencyAmount(value);
					break;
				case "apa35":
					payment.setCurrencyAlamount(value);
					break;
				case "amt":
					payment.setCurrencyUnamount(value);
					break;
				case "apa25":
					payment.setSummary(value);
					break;
				case "apa251":
					payment.setNote(value);
					break;
				default:
					break;
				}
			}
		}
		
		return valid;
	}

	private static void extracted() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
//		HttpPost httpPost = new HttpPost("http://10.98.1.220/websvr/fishare.asmx/get_aahData");//总账
		HttpPost httpPost = new HttpPost("http://10.98.1.220/websvr/fishare.asmx/get_apBusData");//AP

		httpPost.addHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

		String param = "gbcode=A065028&year=2018&month=3";
		httpPost.setEntity(new StringEntity(param, HTTP.UTF_8));

		CloseableHttpResponse response = httpclient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		String xmlString = EntityUtils.toString(entity, HTTP.UTF_8);
//		if (StringUtils.isNotEmpty(xmlString) && xmlString.length()<100) {
			System.out.println("xmlString:"+xmlString);
//		}

		/*File file = new File("C:\\Users\\liangchen\\Desktop\\workspace\\fit\\src\\foxconn\\test\\entity.xml");
		FileReader fileReader=new FileReader(file);
		BufferedReader br=new BufferedReader(fileReader);
		StringBuffer message = new StringBuffer();  
		String line = null;  
		while((line = br.readLine()) != null) {  
			message.append(line).append("\n");
		}  
		String xmlString=message.toString();*/
		
		/*SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
		Element root = document.getRootElement();
		List<DefaultElement> elements = root.elements();
		DefaultElement element = elements.get(1);
		List content = element.content();
		for (Object object : content) {
			if (object instanceof DefaultElement) {
				DefaultElement NewDataSet=(DefaultElement)object;
				List content1 = NewDataSet.content();
				for (Object object1 : content1) {
					if (object1 instanceof DefaultElement) {
						DefaultElement ds=(DefaultElement)object1;
						List content2 = ds.content();
						for (Object object2 : content2) {
							if (object2 instanceof DefaultElement) {
								DefaultElement text=(DefaultElement)object2;
								String defaultText = text.getTextTrim();
								System.out.print(text.getName()+"="+defaultText+",");
							}
						}
						System.out.println();
					}
				}
			}
		}*/
	}

	private static void connectSQLServer() {
		String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String url = "jdbc:sqlserver://10.98.7.90:1433;databaseName=FIT_BI";
		String user = "fitbi";
		String password = "fitbi";
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean flag = false;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			String sql = "select 1 from dual";
			pstm = con.prepareStatement(sql);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int empno = rs.getInt(0);
			}

			flag = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				if (con != null && (!con.isClosed())) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (flag) {
			System.out.println("fail");
		} else {
			System.out.println("success");
		}
	}


	public static List<List<String>> ifRepeat(List<List<String>> insertdataList){

		List<List<String>> afList=new ArrayList<List<String>>();
		for (List<String> dataList : insertdataList) {
			if(afList.size()==0){
				afList.add(dataList);
			}else{
				for (List<String> afData : afList) {
					if(!afData.get(0).equals(dataList.get(0))){
						afList.add(dataList);
						break;
					}
				}
			}
		}
		return afList;
	}



}
