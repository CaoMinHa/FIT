package foxconn.fit.entity.base;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class AjaxResult {
	
	private Map<Object, Object> result;

	public AjaxResult() {
		 result= new HashMap<Object, Object>();
		 result.put("flag", "success");
		 result.put("msg", "操作成功");
	}
	
	public void put(Object key,Object value){
		result.put(key, value);
	}
	
	public void remove(Object key){
		result.remove(key);
	}
	
	public Map<Object, Object> getResult(){
		return this.result;
	}
	
	public String getJson(){
		return JSONObject.fromObject(result).toString();
	}
	
}
