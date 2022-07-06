package foxconn.fit.service.base;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.base.UserDao;
import foxconn.fit.entity.base.EnumUserType;
import foxconn.fit.entity.base.User;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserService extends BaseService<User> implements UserDetailsService {

	private Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private UserDao userDAO;

	@Override
	public BaseDaoHibernate<User> getDao() {
		return userDAO;
	}
	
	public User getEnableByUsername(String username) throws Exception{
		return userDAO.getEnableByUsername(username);
	}
	
	public User getByUsername(String username) throws Exception{
		return userDAO.getByUsername(username);
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user = null;
		try {
			user = userDAO.getEnableByUsername(username);
		} catch (Exception e) {
			logger.error("查询用户失败:",e);
			throw new UsernameNotFoundException("用户名不存在(User Not Found)");
		}

		if (user == null) {
			throw new UsernameNotFoundException("用户名不存在(User Not Found)");
		}

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		EnumUserType type = user.getType();
		if (EnumUserType.Admin==type) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}else if (EnumUserType.HFM==type) {
			authorities.add(new SimpleGrantedAuthority("ROLE_HFM"));
		}else if(EnumUserType.BI==type){
			authorities.add(new SimpleGrantedAuthority("ROLE_BI"));
		}else if(EnumUserType.Budget==type){
			authorities.add(new SimpleGrantedAuthority("ROLE_Budget"));
		}

		UserDetails userDetails = new UserDetailImpl(username, user.getPassword(),user.getCorporationCode(),user.getEntity(),user.getEbs(),user.getMenus(),user.getPoCenter(), authorities);
		return userDetails;
	}

}
