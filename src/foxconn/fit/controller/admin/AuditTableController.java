package foxconn.fit.controller.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.hfm.AuditColumns;
import foxconn.fit.entity.hfm.AuditKey;
import foxconn.fit.entity.hfm.AuditTable;
import foxconn.fit.service.hfm.AuditTableService;
import foxconn.fit.util.ExceptionUtil;

@Controller
@RequestMapping("/admin/table")
public class AuditTableController extends BaseController{

	@Autowired
	private AuditTableService auditTableService;
	
	@RequestMapping(value = "/index")
	public String index(Model model,PageRequest pageRequest) {
		return "admin/table/index";
	}
	
	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest) {
		try {
			Page<AuditTable> page = auditTableService.findPageByHQL(pageRequest, null);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询明细配置表列表失败 : ", e);
		}
		return "/admin/table/list";
	}
	
	@RequestMapping(value="/detail")
	public String detail(PageRequest pageRequest,Model model,String tableName){
		try {
			Assert.hasText(tableName, "表名不能为空");
			AuditTable auditTable = auditTableService.get(tableName);
			model.addAttribute("auditTable", auditTable);
		} catch (Exception e) {
			logger.error("查询明细配置表信息失败 : ", e);
		}
		
		return "/admin/table/detail";
	}
	
	@RequestMapping(value="/delete")
	@ResponseBody
	public String delete(HttpServletRequest request,AjaxResult ajaxResult,Model model,String tableName){
		ajaxResult.put("msg", "删除明细配置表成功");
		try {
			AuditTable auditTable = auditTableService.get(tableName);
			if (auditTable!=null) {
				auditTableService.delete(tableName);
			}
		} catch (Exception e) {
			logger.error("删除明细配置表失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "删除明细配置表失败 : " + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/add")
	@ResponseBody
	public String add(PageRequest pageRequest,AjaxResult ajaxResult,String tableName){
		ajaxResult.put("msg", "添加明细配置表成功");
		try {
			Assert.hasText(tableName, "表名不能为空");
			String tname=tableName.trim().toUpperCase();
			
			AuditTable auditTable = auditTableService.get(tname);
			Assert.isTrue(auditTable==null, "表名已经存在");
			
			String tableSql="select table_name,comments from user_tab_comments where table_name='"+tname+"'";
			List<Object[]> tableList = auditTableService.listBySql(tableSql);
			if (tableList.isEmpty()) {
				ajaxResult.put("flag", "fail");
				ajaxResult.put("msg", "明细表【"+tableName+"】在数据库中不存在");
				return ajaxResult.getJson();
			}
			Object[] table = tableList.get(0);
			auditTable = new AuditTable(table[0].toString(),table[1].toString());
			
			String columnSql="select t.COLUMN_NAME,t.DATA_TYPE,c.COMMENTS,t.NULLABLE from user_tab_columns t,all_col_comments c where t.COLUMN_NAME=c.COLUMN_NAME and t.table_name='"+tname+"' and c.TABLE_NAME='"+tname+"' order by t.COLUMN_ID";
			List<Object[]> auditColumnsList = auditTableService.listBySql(columnSql);
			for (int i = 0; i < auditColumnsList.size(); i++) {
				Object[] auditColumns = auditColumnsList.get(i);
				boolean nullable="Y".equals(auditColumns[3])?true:false;
				AuditColumns auditColumn = new AuditColumns(auditColumns[0].toString(),auditColumns[1].toString(),auditColumns[2].toString(),i,nullable);
				auditColumn.setTable(auditTable);
				auditTable.getColumns().add(auditColumn);
			}
			String keySql="select c.COLUMN_NAME,c.COMMENTS from user_ind_columns i,all_col_comments c where i.COLUMN_NAME=c.COLUMN_NAME and c.TABLE_NAME='"+tname+"' and index_name = (select index_name from user_indexes where table_name='"+tname+"' and rownum=1) order by i.COLUMN_POSITION";
			List<Object[]> auditKeysList = auditTableService.listBySql(keySql);
			if (auditKeysList!=null && auditKeysList.size()>0) {
				for (int i = 0; i < auditKeysList.size(); i++) {
					Object[] auditKeys = auditKeysList.get(i);
					String columnName = auditKeys[0].toString();
					String dataType=null;
					String comments = auditKeys[1].toString();
					int serial=0;
					for (AuditColumns column : auditTable.getColumns()) {
						if (column.getColumnName().equals(columnName)) {
							dataType=column.getDataType();
							serial=column.getSerial();
							break;
						}
					}
					AuditKey auditKey = new AuditKey(columnName,dataType,comments,serial);
					auditKey.setTable(auditTable);
					auditTable.getKeys().add(auditKey);
				}
			}
			
			auditTableService.save(auditTable);
		}catch (Exception e) {
			logger.error("添加明细配置表失败", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "添加明细配置表失败 : " + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/refresh")
	@ResponseBody
	public String refresh(HttpServletRequest request,AjaxResult ajaxResult,String tableName){
		ajaxResult.put("msg", "更新成功");
		try {
			Assert.hasText(tableName, "表名不能为空");
			AuditTable auditTable = auditTableService.get(tableName);
			if (auditTable!=null) {
				auditTableService.delete(tableName);
			}
			
			String tableSql="select table_name,comments from user_tab_comments where table_name='"+tableName+"'";
			List<Object[]> tableList = auditTableService.listBySql(tableSql);
			if (tableList.isEmpty()) {
				ajaxResult.put("flag", "fail");
				ajaxResult.put("msg", "明细表【"+tableName+"】在数据库中不存在");
				return ajaxResult.getJson();
			}
			Object[] table = tableList.get(0);
			auditTable = new AuditTable(table[0].toString(),table[1].toString());
			
			String columnSql="select t.COLUMN_NAME,t.DATA_TYPE,c.COMMENTS,t.NULLABLE from user_tab_columns t,all_col_comments c where t.COLUMN_NAME=c.COLUMN_NAME and t.table_name='"+tableName+"' and c.TABLE_NAME='"+tableName+"' order by t.COLUMN_ID";
			List<Object[]> auditColumnsList = auditTableService.listBySql(columnSql);
			for (int i = 0; i < auditColumnsList.size(); i++) {
				Object[] auditColumns = auditColumnsList.get(i);
				boolean nullable="Y".equals(auditColumns[3])?true:false;
				AuditColumns auditColumn = new AuditColumns(auditColumns[0].toString(),auditColumns[1].toString(),auditColumns[2].toString(),i,nullable);
				auditColumn.setTable(auditTable);
				auditTable.getColumns().add(auditColumn);
			}
			String keySql="select c.COLUMN_NAME,c.COMMENTS from user_ind_columns i,all_col_comments c where i.COLUMN_NAME=c.COLUMN_NAME and c.TABLE_NAME='"+tableName+"' and index_name = (select index_name from user_indexes where table_name='"+tableName+"' and rownum=1) order by i.COLUMN_POSITION";
			List<Object[]> auditKeysList = auditTableService.listBySql(keySql);
			if (auditKeysList!=null && auditKeysList.size()>0) {
				for (int i = 0; i < auditKeysList.size(); i++) {
					Object[] auditKeys = auditKeysList.get(i);
					String columnName = auditKeys[0].toString();
					String dataType=null;
					String comments = auditKeys[1].toString();
					int serial=0;
					for (AuditColumns column : auditTable.getColumns()) {
						if (column.getColumnName().equals(columnName)) {
							dataType=column.getDataType();
							serial=column.getSerial();
							break;
						}
					}
					AuditKey auditKey = new AuditKey(columnName,dataType,comments,serial);
					auditKey.setTable(auditTable);
					auditTable.getKeys().add(auditKey);
				}
			}
			
			auditTableService.save(auditTable);
		}catch (Exception e) {
			logger.error("更新明细配置表信息失败", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "更新明细配置表信息失败 : " + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/update")
	@ResponseBody
	public String update(HttpServletRequest request,AjaxResult ajaxResult,Model model,String tableName,boolean[] locked){
		ajaxResult.put("msg", "更新锁定列信息成功");
		try {
			AuditTable auditTable = auditTableService.get(tableName);
			if (auditTable!=null) {
				List<AuditColumns> columns = auditTable.getColumns();
				Assert.isTrue(columns.size()==locked.length, "列数量不一致");
				for (int i = 0; i < locked.length; i++) {
					columns.get(i).setLocked(locked[i]);
				}
				
				auditTableService.update(auditTable);
			}
		} catch (Exception e) {
			logger.error("更新锁定列信息失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "更新锁定列信息失败 : " + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
}