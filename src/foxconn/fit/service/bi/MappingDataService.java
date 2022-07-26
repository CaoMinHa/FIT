package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.ebs.ParameterDao;
import foxconn.fit.entity.ebs.Parameter;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class MappingDataService extends BaseService<Parameter>{
	public org.apache.commons.logging.Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ParameterDao userDao;
	@Autowired
	private PoTableService poTableService;

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

	public void saveBatch(String tableName,List<String> columnList,List<List<String>> insertDataList) {
		String codes="";
		for (List<String> list : insertDataList) {
			codes+="'"+list.get(0)+"',";
		}
		if(codes.length()>0){
			codes=codes.substring(0,codes.length()-1);
			String column=columnList.get(0);
			String deleteSql=" delete from "+tableName+" where "+column+" in ("+codes+")";
			System.out.println(deleteSql);
			userDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		}
		for (List<String> list : insertDataList) {
			String insertSql = "insert into " + tableName +"(";
			for (int i = 0; i < columnList.size(); i++) {
				insertSql+=columnList.get(i)+",";
			}
			String insert="";
			if("CUX_PO_SINGLESOURCE_PN_MAPPING".equalsIgnoreCase(tableName)||"CUX_RT_ACCOUNT_MAPPING".equalsIgnoreCase(tableName)||"CUX_RT_SALES_ACCOUNT_MAPPING".equalsIgnoreCase(tableName)){
				insertSql+=" LAST_UPDATED_BY ,ID ) values( ";
				insert=",'"+SecurityUtils.getLoginUsername()+"','"+ UUID.randomUUID()+"'";
			}else if("CUX_INTERNERL_VENDOR".equalsIgnoreCase("CUX_INTERNERL_VENDOR")){
				insertSql+=" CREATED_BY,LAST_UPDATED_BY ) values ( ";
				insert=",'"+SecurityUtils.getLoginUsername()+"','"+SecurityUtils.getLoginUsername()+"'";
			}else{
				insertSql+=" LAST_UPDATED_BY ) values ( ";
				insert=",'"+SecurityUtils.getLoginUsername()+"'";
			}
			for (int i = 0; i < list.size(); i++) {
				insertSql+="'"+list.get(i)+"',";
			}
			insertSql=insertSql.substring(0,insertSql.length()-1)+insert+")";
			System.out.println(insertSql);
			userDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
		}
	}
	public void insert(String formVal,String type){
		String sql="insert into ";
		String val="";
		if("Account,CUX_RT_ACCOUNT_MAPPING".equals(type)){
			sql+=" CUX_RT_ACCOUNT_MAPPING(";
			String[] params = formVal.split("&");
			for (String param : params) {
				String columnName = param.substring(0,param.indexOf("="));
				String columnValue = param.substring(param.indexOf("=")+1).trim();
				if(columnName.equals("SALES_AREA")){
					String delete="delete from CUX_RT_ACCOUNT_MAPPING where SALES_AREA = ('"+columnValue+"')";
					userDao.getSessionFactory().getCurrentSession().createSQLQuery(delete).executeUpdate();
				}
				sql+=columnName+",";
				val+="'"+columnValue+"',";
			}
			sql=sql+"LAST_UPDATED_BY,ID ) values("+val+"'"+SecurityUtils.getLoginUsername()+"','"+ UUID.randomUUID()+"')";
			userDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		}else if("Sales_Account,CUX_RT_SALES_ACCOUNT_MAPPING".equals(type)){
			sql+=" CUX_RT_SALES_ACCOUNT_MAPPING(";
			String[] params = formVal.split("&");
			for (String param : params) {
				String columnName = param.substring(0,param.indexOf("="));
				String columnValue = param.substring(param.indexOf("=")+1).trim();
				if(columnName.equals("SALES_AREA")){
					String delete="delete from CUX_RT_ACCOUNT_MAPPING where SALES_AREA = ('"+columnValue+"')";
					userDao.getSessionFactory().getCurrentSession().createSQLQuery(delete).executeUpdate();
				}
				sql+=columnName+",";
				val+="'"+columnValue+"',";
			}
			sql=sql+"LAST_UPDATED_BY,ID ) values("+val+"'"+SecurityUtils.getLoginUsername()+"','"+ UUID.randomUUID()+"')";
			userDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		}
	}

}
