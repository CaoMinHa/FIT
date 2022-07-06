package foxconn.fit.service.bi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.bi.ChannelDao;
import foxconn.fit.entity.bi.Channel;
import foxconn.fit.service.base.BaseService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ChannelService extends BaseService<Channel>{

	@Autowired
	private ChannelDao channelDao;
	
	@Override
	public BaseDaoHibernate<Channel> getDao() {
		return channelDao;
	}
	
	public void saveBatch(List<Channel> list) throws Exception{
		String deleteSql="delete from FIT_Channel";
		
		channelDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		
		for (int i = 0; i < list.size(); i++) {
			channelDao.save(list.get(i));
			
			if ((i+1)%1000==0) {
				channelDao.getHibernateTemplate().flush();
				channelDao.getHibernateTemplate().clear();
			}
		}
	}

}
