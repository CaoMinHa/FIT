package foxconn.fit.service.bi;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.OutsourceActualNumberDao;
import foxconn.fit.entity.bi.OutsourceActualNumber;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class OutsourceActualNumberService extends BaseService<OutsourceActualNumber>{

	@Autowired
	private OutsourceActualNumberDao outsourceActualNumberDao;
	
	@Override
	public BaseDaoHibernate<OutsourceActualNumber> getDao() {
		return outsourceActualNumberDao;
	}
	
	public void saveBatch(List<OutsourceActualNumber> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_Outsource_Actual_Number "+codeCondition;
		
		outsourceActualNumberDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			outsourceActualNumberDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				outsourceActualNumberDao.getHibernateTemplate().flush();
				outsourceActualNumberDao.getHibernateTemplate().clear();
			}
		}
	}
	
	public Map<String,String> synchronize(String year, String month) throws Exception {
		Map<String,String> map=new HashMap<String, String>();
		Connection c = SessionFactoryUtils.getDataSource(outsourceActualNumberDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call BIDEV.PETL_MAIN.PM_LOAD_MONTHLY_EX(?,?,?)}");  
		cs.setInt(1, Integer.parseInt(year+month));
		//需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效  
		cs.registerOutParameter(2, java.sql.Types.VARCHAR);  
		cs.registerOutParameter(3, java.sql.Types.VARCHAR);  
		cs.execute();  
		int code = cs.getInt(2);
		String message = cs.getString(3);
		
		cs.close();
		c.close();
		
		map.put("code", code+"");
		map.put("message", message);
		
		return map;
	}

}
