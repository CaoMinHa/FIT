package foxconn.fit.service.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.FlushMode;
import org.hibernate.classic.Session;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.base.PropertyFilter;

@SuppressWarnings({"unchecked","rawtypes"})
public abstract class BaseService<T> {
	
	public abstract BaseDaoHibernate<T> getDao();
	
	public Page<T> findPageByHQL(PageRequest pageRequest,List<PropertyFilter> filters) throws Exception{
		return getDao().findPageByHQL(pageRequest, filters);
	}
	
	public Page<T> findPageByFilter(PageRequest pageRequest,List<PropertyFilter> filters) throws Exception{
		return getDao().findPageByFilter(pageRequest, filters);
	}
	
	public List<T> listByHQL(List<PropertyFilter> filters,PageRequest pageRequest) throws Exception{
		return getDao().listByHQL(filters,pageRequest);
	}
	
	public List<T> listByFilter(List<PropertyFilter> filters,PageRequest pageRequest) throws Exception{
		return getDao().listByFilter(filters,pageRequest);
	}
	
	public Page<Object[]> findPageBySql(PageRequest pageRequest,String sql){
		return getDao().findPageBySql(pageRequest, sql);
	}
	
	public Page<Object[]> findPageBySql(PageRequest pageRequest,String sql,Class entity){
		return getDao().findPageBySql(pageRequest, sql, entity);
	}
	
	public Page<Map> findMapPageBySql(PageRequest pageRequest,String sql){
		return getDao().findMapPageBySql(pageRequest, sql);
	}
	
	public List listBySql(String sql,Class entity) {
		return getDao().listBySql(sql, entity);
	}
	
	public List listBySql(String sql) {
		return getDao().listBySql(sql);
	}
	
	public List<Map> listMapBySql(String sql) {
		return getDao().listMapBySql(sql);
	}
	
	public List<T> loadAll() throws Exception{
		return getDao().loadAll();
	}
	
	public T get(Serializable id) throws Exception{
		return getDao().get(id);
	}
	
	public void save(T entity) throws Exception{
		Session session = getDao().getSessionFactory().getCurrentSession();
		session.setFlushMode(FlushMode.AUTO);
		
		getDao().save(entity);
		
		session.flush();
	}
	
	public void update(T entity) throws Exception{
		Session session = getDao().getSessionFactory().getCurrentSession();
		session.setFlushMode(FlushMode.AUTO);
		
		getDao().update(entity);
		
		session.flush();
	}
	
	public void delete(Serializable id) throws Exception{
		Session session = getDao().getSessionFactory().getCurrentSession();
		session.setFlushMode(FlushMode.AUTO);
		
		getDao().delete(id);
		
		session.flush();
	}
	
	public void saveCheckExist(Collection<String> list) throws Exception{
		String deleteSql="delete from FIT_CHECK_EXIST";
		getDao().getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		int i=1;
		for (String value : list) {
			String insertSql="insert into FIT_CHECK_EXIST(value) values('"+value+"')";
			getDao().getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
			
			if (i++%1000==0) {
				getDao().getHibernateTemplate().flush();
				getDao().getHibernateTemplate().clear();
			}
		}
	}
	
}
