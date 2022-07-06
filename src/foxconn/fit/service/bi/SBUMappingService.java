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
import foxconn.fit.dao.bi.SBUMappingDao;
import foxconn.fit.entity.bi.SBUMapping;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class SBUMappingService extends BaseService<SBUMapping>{

	@Autowired
	private SBUMappingDao sbuMappingDao;
	
	@Override
	public BaseDaoHibernate<SBUMapping> getDao() {
		return sbuMappingDao;
	}



	public List<String> findSbus(){
		List<String> commoditys = sbuMappingDao.listBySql("select distinct NEW_SBU_NAME from fit_sbu_mapping");
		return commoditys;
	}
	
	public void saveBatch(List<SBUMapping> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_SBU_Mapping "+codeCondition;
		sbuMappingDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			sbuMappingDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				sbuMappingDao.getHibernateTemplate().flush();
				sbuMappingDao.getHibernateTemplate().clear();
			}
		}
	}
	
	public Map<String,String> synchronize(String year, String month,String version) throws Exception {
		Map<String,String> map=new HashMap<String, String>();
		Connection c = SessionFactoryUtils.getDataSource(sbuMappingDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call BIDEV.PPL_MAIN.main_prc(?,?,?,?)}");  
		cs.setString(1, year+month);
		cs.setString(2, version);
		cs.registerOutParameter(3, java.sql.Types.INTEGER);  
		cs.registerOutParameter(4, java.sql.Types.VARCHAR);  
		cs.execute();  
		int code = cs.getInt(3);
		String message = cs.getString(4);
		
		cs.close();
		c.close();
		
		map.put("code", code+"");
		map.put("message", message);
		
		return map;
	}

}
