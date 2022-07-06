package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.ProductBCGDao;
import foxconn.fit.entity.bi.ProductBCG;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductBCGService extends BaseService<ProductBCG>{

	@Autowired
	private ProductBCGDao productBCGDao;
	
	@Override
	public BaseDaoHibernate<ProductBCG> getDao() {
		return productBCGDao;
	}
	
	public void saveBatch(List<ProductBCG> list,String codeCondition) throws Exception{
		String deleteSql="delete from FIT_Product_BCG "+codeCondition;
		
		productBCGDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			productBCGDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				productBCGDao.getHibernateTemplate().flush();
				productBCGDao.getHibernateTemplate().clear();
			}
		}
	}

}
