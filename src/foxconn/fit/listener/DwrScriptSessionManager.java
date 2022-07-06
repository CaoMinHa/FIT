package foxconn.fit.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.impl.DefaultScriptSessionManager;

public class DwrScriptSessionManager extends DefaultScriptSessionManager{

	private static Log logger = LogFactory.getLog(DwrScriptSessionManager.class);
	
	public DwrScriptSessionManager() {
		// 绑定一个ScriptSession增加销毁事件的监听器
		this.addScriptSessionListener(new DwrScriptSessionListener());
		logger.info("bind DwrScriptSessionListener");
	}
	
}
