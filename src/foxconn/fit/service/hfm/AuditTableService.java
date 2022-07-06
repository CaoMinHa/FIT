package foxconn.fit.service.hfm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.AuditTableDao;
import foxconn.fit.entity.base.EnumGenerateType;
import foxconn.fit.entity.hfm.AuditColumns;
import foxconn.fit.entity.hfm.AuditKey;
import foxconn.fit.entity.hfm.AuditTable;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class AuditTableService extends BaseService<AuditTable>{

	public static final String UPLOAD_VALIDATE="UPLOAD_VAL";
	public static final String DATA_VALIDATE="DATA_VAL";
	
	@Autowired
	private AuditTableDao auditTableDao;
	
	@Override
	public BaseDaoHibernate<AuditTable> getDao() {
		return auditTableDao;
	}

	public void saveAuditTempData(Map<AuditTable, List<List<String>>> dataMap, String year, String period, String entity) {
		int cnt=1;
		for (AuditTable auditTable : dataMap.keySet()) {
			List<AuditColumns> columns = auditTable.getColumns();
			String tableName=auditTable.getTableName()+"_T";
			String deleteSql="delete from "+tableName;
			auditTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
			
			List<List<String>> dataList = dataMap.get(auditTable);
			for (List<String> data : dataList) {
				String generateType = data.get(0);
				String columnStr="";
				for (AuditColumns column : columns) {
					columnStr+=column.getColumnName()+",";
				}
				columnStr=columnStr.substring(0,columnStr.length()-1);
				String valueStr="'"+generateType+"',";
				for (int i = 1; i < data.size(); i++) {
					if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
						valueStr+="to_number('"+data.get(i)+"'),";
					}else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
						valueStr+="to_date('"+data.get(i)+"','dd/mm/yyyy'),";
					}else{
						valueStr+="'"+data.get(i)+"',";
					}
				}
				valueStr=valueStr.substring(0,valueStr.length()-1);
				String insertSql="insert into "+tableName+"("+columnStr+") values("+valueStr+")";
				auditTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
				cnt++;
				if (cnt%1000==0) {
					auditTableDao.getHibernateTemplate().flush();
					auditTableDao.getHibernateTemplate().clear();
				}
			}
		}
	}
	
	public void saveAuditData(Map<AuditTable, List<List<String>>> dataMap, String year, String period, String entity) {
		int cnt=1;
		for (AuditTable auditTable : dataMap.keySet()) {
			List<AuditColumns> columns = auditTable.getColumns();
			List<AuditKey> keys = auditTable.getKeys();
			if (keys!=null && keys.size()>0) {
				String deleteSql="delete from "+auditTable.getTableName()+
						" where "+columns.get(0).getColumnName()+"='"+EnumGenerateType.M.getCode()+"'"+
						"  and "+columns.get(1).getColumnName()+"='"+year+
						"' and "+columns.get(2).getColumnName()+"='"+period+
						"' and "+columns.get(3).getColumnName()+"='"+entity+"'";
				auditTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
				
				List<List<String>> dataList = dataMap.get(auditTable);
				for (List<String> data : dataList) {
					String generateType = data.get(0);
					if (EnumGenerateType.M.getCode().equals(generateType)) {
						String columnStr="";
						for (AuditColumns column : columns) {
							columnStr+=column.getColumnName()+",";
						}
						columnStr=columnStr.substring(0,columnStr.length()-1);
						String valueStr="'"+generateType+"',";
						for (int i = 1; i < data.size(); i++) {
							if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
								valueStr+="to_number('"+data.get(i)+"'),";
							}else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
								valueStr+="to_date('"+data.get(i)+"','dd/mm/yyyy'),";
							}else{
								valueStr+="'"+data.get(i)+"',";
							}
						}
						valueStr=valueStr.substring(0,valueStr.length()-1);
						String insertSql="insert into "+auditTable.getTableName()+"("+columnStr+") values("+valueStr+")";
						auditTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
						cnt++;
						if (cnt%1000==0) {
							auditTableDao.getHibernateTemplate().flush();
							auditTableDao.getHibernateTemplate().clear();
						}
					}else{
						String updateSql="update "+auditTable.getTableName();
						String setSql=" set ";
						String whereSql=" where ";
						for (AuditKey auditKey : keys) {
							String columnName = auditKey.getColumnName();
							String value = data.get(auditKey.getSerial());
							if (auditKey.getDataType().equalsIgnoreCase("number")) {
								whereSql+=columnName+"=to_number('"+value+"') and ";
							}else if (auditKey.getDataType().equalsIgnoreCase("date")) {
								whereSql+="to_char("+columnName+",'dd/mm/yyyy')='"+value+"' and ";
							}else{
								whereSql+=columnName+"='"+value+"' and ";
							}
						}
						for (int i = 0; i < columns.size(); i++) {
							AuditColumns column = columns.get(i);
							String columnName = column.getColumnName();
							String value = data.get(column.getSerial());
							if (StringUtils.isNotEmpty(value)) {
								if (i>4 && !column.getLocked()) {
									if (column.getDataType().equalsIgnoreCase("date")) {
										setSql+=columnName+"=to_date('"+value+"','dd/mm/yyyy'),";
									}else{
										setSql+=columnName+"='"+value+"',";
									}
								}
							}
						}
						setSql=setSql.substring(0,setSql.length()-1);
						whereSql=whereSql.substring(0,whereSql.length()-4);
						updateSql+=setSql+whereSql;
						auditTableDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
						cnt++;
						if (cnt%1000==0) {
							auditTableDao.getHibernateTemplate().flush();
							auditTableDao.getHibernateTemplate().clear();
						}
					}
				}
			}else {
				String deleteSql="delete from "+auditTable.getTableName()+
						" where "+columns.get(0).getColumnName()+" in ('"+EnumGenerateType.M.getCode()+"','"+EnumGenerateType.AM.getCode()+"')"+
						"  and "+columns.get(1).getColumnName()+"='"+year+
						"' and "+columns.get(2).getColumnName()+"='"+period+
						"' and "+columns.get(3).getColumnName()+"='"+entity+"'";
				auditTableDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
				
				List<List<String>> dataList = dataMap.get(auditTable);
				for (List<String> data : dataList) {
					String generateType = data.get(0);
					String columnStr="";
					for (AuditColumns column : columns) {
						columnStr+=column.getColumnName()+",";
					}
					columnStr=columnStr.substring(0,columnStr.length()-1);
					String valueStr="'"+generateType+"',";
					for (int i = 1; i < data.size(); i++) {
						if (columns.get(i).getDataType().equalsIgnoreCase("number")) {
							valueStr+="to_number('"+data.get(i)+"'),";
						}else if (columns.get(i).getDataType().equalsIgnoreCase("date")) {
							valueStr+="to_date('"+data.get(i)+"','dd/mm/yyyy'),";
						}else{
							valueStr+="'"+data.get(i)+"',";
						}
					}
					valueStr=valueStr.substring(0,valueStr.length()-1);
					String insertSql="insert into "+auditTable.getTableName()+"("+columnStr+") values("+valueStr+")";
					auditTableDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
					cnt++;
					if (cnt%1000==0) {
						auditTableDao.getHibernateTemplate().flush();
						auditTableDao.getHibernateTemplate().clear();
					}
				}
			}
			
		}
	}
	

	public String validate(String tableName, String year, String period, String entity,String type,Locale locale) throws Exception {
		Connection c = SessionFactoryUtils.getDataSource(auditTableDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call CUX_AUDIT_DATA_PKG.validation(?,?,?,?,?,?,?,?)}");  
		cs.setString(1, tableName);
		cs.setString(2, type);
		cs.setString(3, locale!=null?locale.toString():"zh_CN");
		cs.setString(4, year);
		cs.setString(5, period);
		cs.setString(6, entity);
		//需要获取输出参数时，必须注册输出参数，否则直接条用cs.getInt(4)时会报索引列无效  
		cs.registerOutParameter(7, java.sql.Types.INTEGER);  
		cs.registerOutParameter(8, java.sql.Types.VARCHAR);  
		cs.execute();  
		int code = cs.getInt(7);
		String message = cs.getString(8);
		
		cs.close();
		c.close();
		
		if (code!=0) {
			return message;
		}
		
		return "";
	}
	
	public void generateConsolidation(String year, String period,String procedureName) throws Exception {
		Connection c = SessionFactoryUtils.getDataSource(auditTableDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call CUX_AUDIT_DATA_PKG."+procedureName.toLowerCase()+"(?,?)}");  
		cs.setString(1, year);
		cs.setString(2, period);
		cs.execute();  
	}

	public String dataImport(String year, String period,String entity,Locale locale) throws Exception{
		String lang="CN";
		if (locale!=null && locale.toString().equals("en_US")) {
			lang="EN";
		}
		Connection c = SessionFactoryUtils.getDataSource(auditTableDao.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call CUX_AUDIT_DATA_PKG.main_integration(?,?,?,?,?,?,?)}");  
		cs.setString(1, year);
		cs.setString(2, period);
		cs.setString(3, entity);
		cs.setString(4, lang);
		cs.setString(5, "All");
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

}
