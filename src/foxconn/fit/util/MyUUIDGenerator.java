package foxconn.fit.util;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.UUIDGenerator;

/**
 * UUID
 * 
 * @author chenliang
 * 
 */
public class MyUUIDGenerator extends UUIDGenerator{

	@Override
	public Serializable generate(SessionImplementor session, Object object)
			throws HibernateException {
		return super.generate(session, object).toString().replace("-", "");
	}
	
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static void main(String[] args) {
		System.out.println(getUUID());
	}
	
}
