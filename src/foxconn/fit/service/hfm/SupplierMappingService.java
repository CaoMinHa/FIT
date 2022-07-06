package foxconn.fit.service.hfm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.hfm.SupplierMappingDao;
import foxconn.fit.entity.hfm.SupplierMapping;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class SupplierMappingService extends BaseService<SupplierMapping>{

	@Autowired
	private SupplierMappingDao supplierMappingDao;
	
	@Override
	public BaseDaoHibernate<SupplierMapping> getDao() {
		return supplierMappingDao;
	}
	
	public void saveBatch(List<SupplierMapping> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_Supplier_Mapping "+codeCondition;
		
		supplierMappingDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			supplierMappingDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				supplierMappingDao.getHibernateTemplate().flush();
				supplierMappingDao.getHibernateTemplate().clear();
			}
		}
	}

}
