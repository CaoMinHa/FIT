package foxconn.fit.service.base;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.ebs.ParameterDao;
import foxconn.fit.entity.ebs.Parameter;

@Service
@Transactional(rollbackFor = Exception.class)
public class MasterDataService extends BaseService<Parameter>{
	public org.apache.commons.logging.Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ParameterDao userDao;

	@Override
	public BaseDaoHibernate<Parameter> getDao() {
		return userDao;
	}

	public void updateMasterData(String updateSql) {
		userDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
	}

	public String refreshMasterData(String masterData) throws Exception {
		Connection c = SessionFactoryUtils.getDataSource(userDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call EPMEBS.CUX_MASTERDATA_IMPORT_PKG.main(?,?)}");  
		cs.setString(1, masterData);
		cs.registerOutParameter(2, java.sql.Types.VARCHAR);  
		cs.execute();  
		String message = cs.getString(2);
		
		cs.close();
		c.close();
		
		return message;
	}
	
	public void saveBatch(String tableName,List<String> columnList,List<List<String>> dataList) {
		String sql="update "+tableName+" set ";
		for (int i = 0; i < columnList.size(); i++) {
			sql+=columnList.get(i)+"='$"+(i+1)+"',";
		}
		sql=sql.substring(0, sql.length()-1);
		sql+=" where ID='$0'";
		
		for (List<String> list : dataList) {
			String id = list.get(0);
			String updateSql=sql;
			updateSql=updateSql.replaceFirst("\\$0", id);
			for (int j = 1; j < list.size(); j++) {
				updateSql=updateSql.replaceFirst("\\$"+j, list.get(j));
			}
			userDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();

		}
	}

}
