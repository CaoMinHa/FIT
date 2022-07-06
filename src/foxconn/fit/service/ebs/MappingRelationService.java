package foxconn.fit.service.ebs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.ebs.MappingRelationDao;
import foxconn.fit.entity.ebs.MappingRelation;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class MappingRelationService extends BaseService<MappingRelation>{
	
	@Autowired
	private MappingRelationDao mappingRelationDao;
	
	@Override
	public BaseDaoHibernate<MappingRelation> getDao() {
		return mappingRelationDao;
	}
	
	public void saveBatch(List<MappingRelation> list,String condition) throws Exception{
		String deleteSql="delete from CUX_DATAMAP "+condition;
		mappingRelationDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			mappingRelationDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				mappingRelationDao.getHibernateTemplate().flush();
				mappingRelationDao.getHibernateTemplate().clear();
			}
		}
	}

}
