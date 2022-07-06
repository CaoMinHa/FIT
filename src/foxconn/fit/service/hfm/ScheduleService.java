package foxconn.fit.service.hfm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.GeneralLedgerDao;
import foxconn.fit.entity.base.EnumScheduleType;
import foxconn.fit.entity.hfm.GeneralLedger;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ScheduleService extends BaseService<GeneralLedger>{

	@Autowired
	private GeneralLedgerDao generalLedgerDao;
	
	@Override
	public BaseDaoHibernate<GeneralLedger> getDao() {
		return generalLedgerDao;
	}
	
	public String dataTransfer(List<String> codes,int year,int period,EnumScheduleType type) throws Exception{
		String entity="";
		for (String code : codes) {
			entity+="'"+code+"',";
		}
		entity=entity.substring(0, entity.length()-1);
		
		return dataMapping(entity, year, period, type.getProcedureName());
	}

	private String dataMapping(String entity, int year, int period,String procedureName) throws Exception {
		Connection c = SessionFactoryUtils.getDataSource(generalLedgerDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call pkg_fit_hfm_data_insert."+procedureName+"(?,?,?,?,?)}");  
		cs.setString(1, entity);
		cs.setInt(2, year);
		cs.setInt(3, period);
		//需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效  
		cs.registerOutParameter(4, java.sql.Types.VARCHAR);  
		cs.registerOutParameter(5, java.sql.Types.VARCHAR);  
		cs.execute();  
		String status = cs.getString(4);
		String message = cs.getString(5);
		
		cs.close();
		c.close();
		
		if (!"S".equals(status)) {
			return message;
		}
		
		return "";
	}
	
	public String dataMapping(String entity, String year, String period,String type,Locale locale) throws Exception {
		Connection c = SessionFactoryUtils.getDataSource(generalLedgerDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call CUX_HFMDATA_SYNC_PKG.data_sync_prc(?,?,?,?,?,?,?)}");  
		cs.setString(1, entity);
		cs.setString(2, year);
		cs.setString(3, period);
		cs.setString(4, type);
		cs.setString(5, locale!=null?locale.toString():"zh_CN");
		//需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效  
		cs.registerOutParameter(6, java.sql.Types.VARCHAR);  
		cs.registerOutParameter(7, java.sql.Types.VARCHAR);  
		cs.execute();  
		String message = cs.getString(7);
		
		cs.close();
		c.close();
		
		return message;
	}


}
