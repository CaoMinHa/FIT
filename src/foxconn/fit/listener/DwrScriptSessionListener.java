package foxconn.fit.listener;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;

public class DwrScriptSessionListener implements ScriptSessionListener {

	private static Log logger = LogFactory.getLog(DwrScriptSessionListener.class);

	/**
	 * ScriptSession创建事件
	 */
	public void sessionCreated(ScriptSessionEvent event) {
		WebContext webContext = WebContextFactory. get();  
		HttpSession session = webContext.getSession();  
		ScriptSession scriptSession = event.getSession();
		scriptSession.setAttribute("sessionId", session.getId());
		logger.info("创建ScriptSession:"+scriptSession.getId());
	}

	/**
	 * ScriptSession销毁事件
	 */
	public void sessionDestroyed(ScriptSessionEvent event) {
		ScriptSession scriptSession = event.getSession();
		if (scriptSession != null) {
			logger.info("销毁ScriptSession:"+scriptSession.getId());
		}
	}

}