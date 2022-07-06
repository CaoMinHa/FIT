package foxconn.fit.dao.base;

import org.springframework.stereotype.Repository;

import foxconn.fit.entity.base.User;

@Repository
public class UserDao extends BaseDaoHibernate<User> {

	public User getEnableByUsername(String username) throws Exception {
		return (User) findUnique("from User where username=? and enable=true", new String[]{username});
	}
	
	public User getByUsername(String username) throws Exception {
		return (User) findUnique("from User where username=?", new String[]{username});
	}
	
}
