package foxconn.fit.service.ebs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.ebs.ParameterDao;
import foxconn.fit.entity.ebs.Parameter;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ParameterService extends BaseService<Parameter>{

	@Autowired
	private ParameterDao userDao;

	@Override
	public BaseDaoHibernate<Parameter> getDao() {
		return userDao;
	}
	
}
