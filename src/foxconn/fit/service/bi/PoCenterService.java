package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoCenterDao;
import foxconn.fit.entity.bi.PoCenter;
import foxconn.fit.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PoCenterService extends BaseService<PoCenter> {

	@Autowired
	private PoCenterDao poCenterDao;

	@Override
	public BaseDaoHibernate<PoCenter> getDao() {
		return poCenterDao;
	}

	public List<String> findPoCenters() {
		List<String> poCenters = poCenterDao.listBySql("select distinct FUNCTION_NAME from CUX_FUNCTION_COMMODITY_MAPPING");
		return poCenters;
	}

	public List<String> findCommoditys() {
		List<String> commoditys = poCenterDao.listBySql("select distinct COMMODITY_NAME from CUX_FUNCTION_COMMODITY_MAPPING");
		return commoditys;
	}
}