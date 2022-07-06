package foxconn.fit.service.base;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.base.UserLogDao;
import foxconn.fit.entity.base.UserLog;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserLogService extends BaseService<UserLog>{

	@Autowired
	private UserLogDao userLogDAO;

	@Override
	public BaseDaoHibernate<UserLog> getDao() {
		return userLogDAO;
	}
	
	public void cleanHistory(){
		userLogDAO.getSessionFactory().getCurrentSession().createSQLQuery("delete from fit_user_log where operator_time < add_months(sysdate,-6)").executeUpdate();
	}

	public void updateSIXP_INTERFACE(String id, String success, String reportUrl) {
		String sql="update BIDEV.SIXP_INTERFACE set issend='TRUE',issendsuc='"+success+"',reporturl='"+reportUrl+"',modifytime=sysdate where id='"+id+"'";
		userLogDAO.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
	}

	public List<Object[]> listAlarm() {
		return listBySql("select id,system,dept,light,subject,body,reporttime,reportvalue from BIDEV.SIXP_INTERFACE where issend='FALSE'");
	}
	
	public void sendAlarmMail(String error) throws Exception{
		Connection c = SessionFactoryUtils.getDataSource(userLogDAO.getSessionFactory()).getConnection();  
		CallableStatement cs = c.prepareCall("{call bidev.PETL_MAIN.MAIL_SIXP_INTERFACE_EX(?)}");  
		cs.setString(1, error);
		cs.execute();  
		cs.close();
		c.close();
	}
	
}
