package foxconn.fit.service.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.base.DimensionDao;
import foxconn.fit.entity.base.Dimension;
import foxconn.fit.entity.base.EnumDimensionType;

@Service
@Transactional(rollbackFor = Exception.class)
public class DimensionService extends BaseService<Dimension>{

	@Autowired
	private DimensionDao dimensionDao;
	
	@Override
	public BaseDaoHibernate<Dimension> getDao() {
		return dimensionDao;
	}
	
	public void saveBatch(List<Dimension> list,EnumDimensionType type) throws Exception{
		String deleteSql="delete from FIT_DIMENSION where type='"+type.getCode()+"'";
		dimensionDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			dimensionDao.save(list.get(i));
			if ((i+1)%1000==0) {
				dimensionDao.getHibernateTemplate().flush();
				dimensionDao.getHibernateTemplate().clear();
			}
		}
	}

}
