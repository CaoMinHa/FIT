package foxconn.fit.service.hfm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.ebs.ParameterDao;
import foxconn.fit.entity.ebs.Parameter;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class HFMPackageService extends BaseService<Parameter>{
	
	@Value("${jdbc-0.proxool.driver-url}")
	String dburl;
	@Value("${jdbc-0.user}")
	String dbusername;
	@Value("${jdbc-0.password}")
	String dbpassword;
	
	@Autowired
	private ParameterDao userDao;

	@Override
	public BaseDaoHibernate<Parameter> getDao() {
		return userDao;
	}

	public String dataImport(String entity,String year,String period,Locale locale) throws Exception {
		String lang="CN";
		if (locale!=null && locale.toString().equals("en_US")) {
			lang="EN";
		}
		Connection c = SessionFactoryUtils.getDataSource(userDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call CUX_DATA_INTEGRATION_PKG.main(?,?,?,?,?,?,?)}");  
		cs.setString(1, entity);
		cs.setString(2, year);
		cs.setString(3, period);
		cs.setString(4, "ALL");
		cs.setString(5, lang);
		cs.registerOutParameter(6, java.sql.Types.VARCHAR);  
		cs.registerOutParameter(7, java.sql.Types.VARCHAR); 
		cs.execute();  
		String status = cs.getString(6);
		String message = cs.getString(7);
		cs.close();
		c.close();
		
		if ("S".equals(status)) {
			message="";
		}
		return message;
	}
	
	public String dataSync(String entity,String year,String period,Locale locale) throws Exception {
		String lang="CN";
		if (locale!=null && locale.toString().equals("en_US")) {
			lang="EN";
		}
		Connection c = SessionFactoryUtils.getDataSource(userDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call CUX_HFMDATA_SYNC_PKG.data_sync_prc(?,?,?,?,?,?,?)}");  
		cs.setString(1, entity);
		cs.setString(2, year);
		cs.setString(3, period);
		cs.setString(4, "ALL");
		cs.setString(5, lang);
		cs.registerOutParameter(6, java.sql.Types.VARCHAR);  
		cs.registerOutParameter(7, java.sql.Types.VARCHAR); 
		cs.execute();  
		String status = cs.getString(6);
		String message = cs.getString(7);
		cs.close();
		c.close();
		
		if ("S".equals(status)) {
			message="";
		}
		return message;
	}
	
	public Map<String,String> getDBInfo(){
		Map<String,String> map=new HashMap<String, String>();
		map.put("dburl", dburl);
		map.put("dbusername", dbusername);
		map.put("dbpassword", dbpassword);
		return map;
	}
	
}
