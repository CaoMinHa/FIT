package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.PoCenterDao;
import foxconn.fit.dao.bi.PoTableDao;
import foxconn.fit.entity.base.EnumGenerateType;
import foxconn.fit.entity.bi.PoCenter;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoKey;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.BaseService;
import jdk.nashorn.internal.runtime.FindProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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