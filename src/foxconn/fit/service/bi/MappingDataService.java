package foxconn.fit.service.bi;

import foxconn.fit.dao.base.BaseDaoHibernate;
import foxconn.fit.dao.base.UserDao;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.User;
import foxconn.fit.service.base.BaseService;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class MappingDataService extends BaseService<User> {
	public org.apache.commons.logging.Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private UserDao userDao;
	@Autowired
	private InstrumentClassService instrumentClassService;

	@Override
	public BaseDaoHibernate<User> getDao() {
		return userDao;
	}

	/**頁面初始加載**/
	public List<String> index(HttpServletRequest request,String type){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String language=instrumentClassService.getLanguage(locale,"CN","EN");
		List<String> supplierList = this.listBySql("select t.lov_code||','||t.tab_name||'|'||t.lov_desc from CUX_MD_LOV_VALUES t where t.lov_type='"+type+"' and t.enabled_flag='Y' and t.language='"+language+"' ORDER BY to_number(COL_SEQ)");
		return supplierList;
	}
	/**
	 * 獲取選中表查詢條件
	 */
	public List<List<String>> queryMasterData(Locale locale,String masterData){
		String language=instrumentClassService.getLanguage(locale,"CN","EN");
		String masterType=masterData.split(",")[0];
		String sql="SELECT COL_NAME,COL_DESC FROM CUX_PO_MAP_DATA_COLS WHERE CATEGORY = '"+masterType+"' AND LANGUAGE = '"+language+"' AND IS_QUERY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)";
		System.out.println(sql);
		List<List<String>> queryList = this.listBySql(sql);
		return queryList;
	}

	/**
	 * 查詢數據
	 */
	public void list(Model model,String language, PageRequest pageRequest, String masterData, String queryCondition) throws UnsupportedEncodingException {
		String masterType=masterData.split(",")[0];
		String tableName=masterData.split(",")[1];

		List<Object[]> titleList = this.listBySql("SELECT COL_NAME,COL_DESC,READ_WRITE,LOV,DATA_TYPE FROM CUX_PO_MAP_DATA_COLS WHERE CATEGORY = '"+masterType+"' AND LANGUAGE = '"+language+"' AND IS_DISPLAY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)");
		model.addAttribute("titleList", titleList);
		List<Object[]> optionList = this.listBySql("SELECT c.lov,v.lov_code,v.lov_desc FROM CUX_PO_MAP_DATA_COLS c,CUX_MD_LOV_VALUES v "+
				"WHERE c.CATEGORY = '"+masterType+"' AND c.LANGUAGE = '"+language+"' AND c.IS_DISPLAY = 'Y' AND c.ENABLED_FLAG = 'Y' and c.LOV is not null and c.lov=v.lov_type and v.language='"+language+"' and v.enabled_flag='Y' order by c.lov,v.lov_code desc");
		Map<String,String> optionMap=new HashMap<String,String>();
		if (optionList!=null && optionList.size()>0) {
			for (Object[] objects : optionList) {
				String lov=(String) objects[0];
				String lovCode=(String) objects[1];
				String lovDesc=(String) objects[2];
				String value = optionMap.get(lov);
				if (StringUtils.isEmpty(value)) {
					value=lovCode+"-"+lovDesc;
				}else{
					value+=","+lovCode+"-"+lovDesc;
				}
				optionMap.put(lov, value);
			}
		}

		String sql="select id,";
		for (Object[] titleObjects : titleList) {
			Object column = titleObjects[0];
			Object read = titleObjects[2];
			Object lov = titleObjects[3];
			Object dataType = titleObjects[4];
			if (lov!=null && StringUtils.isNotEmpty(lov.toString())) {
				sql+="'"+column+"|"+read+"S|'||nvl("+column+",' ')||'|"+optionMap.get(lov)+"',";
			}else{
				if("DATE".equals(dataType.toString())){
					sql+="'"+column+"|"+read+"|'||to_char("+column+",'yyyy-mm-dd hh24:mi:ss'),";
				}else{
					sql+="'"+column+"|"+read+"|'||"+column+",";
				}
			}
		}
		sql=sql.substring(0, sql.length()-1)+" from "+tableName;
		if (StringUtils.isNotEmpty(queryCondition)) {
			sql+=" where 1=1 ";
			if("CUX_SBU_MAPPING".equalsIgnoreCase(tableName)){
				sql="select distinct ID_SBU ID,'BM_SBU|R|'||OLD_SBU_NAME,'SBU|R|'||NEW_SBU_NAME,'SBU|R|'||STATE,'SBU|R|'||UPDATE_NAME,'SBU|R|'||UPDATE_DATE from bidev.v_if_sbu_mapping where NEW_SBU_NAME in('IDS','EMS','ABS','ACE','ASD','AEC','TSC','APS','CW','FAD','IoT','CIDA','Tengyang','TMTS','FIAD')";
			}
			String[] params = queryCondition.split("&");
			for (String param : params) {
				String columnName = param.substring(0,param.indexOf("="));
				String columnValue = param.substring(param.indexOf("=")+1).trim();
				if (StringUtils.isNotEmpty(columnValue)) {
					columnValue= URLDecoder.decode(columnValue, "UTF-8");
					sql+=" and "+columnName+" like '%"+columnValue+"%'";
				}
			}
			if("CUX_FUNCTION_COMMODITY_MAPPING".equalsIgnoreCase(tableName)){
				sql+=" order by COMMODITY_NO,FUNCTION_NO";
			}else if("CUX_PO_FUNCTION_MAPPING".equalsIgnoreCase(tableName)){
				sql+=" order by FUNCTION_NO";
			}else if("CUX_PO_COMMODITY_MAPPING".equalsIgnoreCase(tableName)){
				sql+=" order by COMMODITY_NO";
			}else if("CUX_INTERNERL_VENDOR".equalsIgnoreCase(tableName)){
				sql+=" order by to_number(NO)";
			}else if("CUX_RT_ACCOUNT_MAPPING".equalsIgnoreCase(tableName)||"CUX_RT_SALES_ACCOUNT_MAPPING".equalsIgnoreCase(tableName)){
				sql+=" order by LAST_UPDATED_DATE desc,ID";
			}else{
				sql+=" order by ID";
			}
		}
		Page<Object[]> page = this.findPageBySql(pageRequest, sql);
		int index=1;
		if(pageRequest.getPageNo()>1){
			index=2;
		}
		model.addAttribute("index", index);
		model.addAttribute("page", page);
		model.addAttribute("masterType", masterType);
	}

	/**
	 * 營收映射表單個修改
	 */
	public void update(String masterData, String updateData){
		String tableName=masterData.split(",")[1];
		if (StringUtils.isNotEmpty(updateData)) {
			String updateSql="update "+tableName+" set ";
			String where="";
			String[] params = updateData.split("￥");
			for (String param : params) {
				String columnName = param.substring(0,param.indexOf("="));
				String columnValue = param.substring(param.indexOf("=")+1).trim();
				if ("ID".equalsIgnoreCase(columnName)) {
					where=" where ID='"+columnValue+"'";
				}else{
					if (StringUtils.isNotEmpty(columnValue)) {
						updateSql+=columnName+"='"+columnValue+"',";
					}
				}
			}
			updateSql=updateSql.substring(0, updateSql.length()-1);
			updateSql+=where;
			this.updateMasterData(updateSql);
		}
	}

	/**映射表數據保存**/
	public void saveBatch(String tableName,List<String> columnList,List<List<String>> insertDataList) {
		String codes="";
		for (List<String> list : insertDataList) {
			codes+="'"+list.get(0)+"',";
		}
		if(codes.length()>0){
			codes=codes.substring(0,codes.length()-1);
			String column=columnList.get(0);
			String deleteSql=" delete from "+tableName+" where "+column+" in ("+codes+")";
			System.out.println(deleteSql);
			userDao.getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
		}
		for (List<String> list : insertDataList) {
			String insertSql = "insert into " + tableName +"(";
			for (int i = 0; i < columnList.size(); i++) {
				insertSql+=columnList.get(i)+",";
			}
			String insert="";
			if("CUX_PO_SINGLESOURCE_PN_MAPPING".equalsIgnoreCase(tableName)||"CUX_RT_ACCOUNT_MAPPING".equalsIgnoreCase(tableName)||"CUX_RT_SALES_ACCOUNT_MAPPING".equalsIgnoreCase(tableName)){
				insertSql+=" LAST_UPDATED_BY ,ID ) values( ";
				insert=",'"+SecurityUtils.getLoginUsername()+"','"+ UUID.randomUUID()+"'";
			}else if("CUX_INTERNERL_VENDOR".equalsIgnoreCase("CUX_INTERNERL_VENDOR")){
				insertSql+=" CREATED_BY,LAST_UPDATED_BY ) values ( ";
				insert=",'"+SecurityUtils.getLoginUsername()+"','"+SecurityUtils.getLoginUsername()+"'";
			}else{
				insertSql+=" LAST_UPDATED_BY ) values ( ";
				insert=",'"+SecurityUtils.getLoginUsername()+"'";
			}
			for (int i = 0; i < list.size(); i++) {
				insertSql+="'"+list.get(i)+"',";
			}
			insertSql=insertSql.substring(0,insertSql.length()-1)+insert+")";
			System.out.println(insertSql);
			userDao.getSessionFactory().getCurrentSession().createSQLQuery(insertSql).executeUpdate();
		}
	}

	/**營收數據上傳**/
	public String upload(AjaxResult result, String masterData, HttpServletRequest request, Locale locale) throws Exception {
		String masterType=masterData.split(",")[0];
		String tableName=masterData.split(",")[1];
		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
		if (mutipartFiles != null && mutipartFiles.size() > 0) {
			MultipartFile file = (MultipartFile) mutipartFiles.values().toArray()[0];

			String suffix = "";
			if (file.getOriginalFilename().lastIndexOf(".") != -1) {
				suffix = file.getOriginalFilename().substring(
						file.getOriginalFilename().lastIndexOf(".") + 1,
						file.getOriginalFilename().length());
				suffix = suffix.toLowerCase();
			}
			if (!"xls".equals(suffix) && !"xlsx".equals(suffix)) {
				result.put("flag", "fail");
				result.put("msg", "請您上傳正確格式的Excel文件");
				return result.getJson();
			}

			Workbook wb=null;
			if ("xls".equals(suffix)) {
				//Excel2003
				wb=new HSSFWorkbook(file.getInputStream());
			}else {
				//Excel2007
				wb=new XSSFWorkbook(file.getInputStream());
			}
			wb.close();

			Sheet sheet = wb.getSheetAt(0);
			int rowNum = sheet.getPhysicalNumberOfRows();
			if (rowNum<4) {
				result.put("flag", "fail");
				result.put("msg", instrumentClassService.getLanguage(locale,"未發現需要處理的數據","No valid data"));
				return result.getJson();
			}
			Row columnRow = sheet.getRow(0);
			Row readRow = sheet.getRow(1);
			Row lovRow = sheet.getRow(2);
			int columnNum = readRow.getPhysicalNumberOfCells()-2;
			String language = columnRow.getCell(0).getStringCellValue();
			String tableNAME = readRow.getCell(0).getStringCellValue();
			Assert.isTrue(tableName.equals(tableNAME), instrumentClassService.getLanguage(locale,"映射表選擇錯誤","Incorrect mapping table selection"));

			List<Integer> indexList=new ArrayList<Integer>();
			List<String> columnList=new ArrayList<String>();
			Map<Integer,String> readMap=new HashMap<Integer, String>();
			Map<Integer,String> lovMap=new HashMap<Integer, String>();
			for (int i = 1; i < columnNum; i++) {
				String read = readRow.getCell(i).getStringCellValue();
				if ("W".equals(read)) {
					String column = columnRow.getCell(i).getStringCellValue();
					columnList.add(column);
					String lov = lovRow.getCell(i).getStringCellValue();
					if (StringUtils.isNotEmpty(lov)) {
						readMap.put(Integer.valueOf(i), "WS");
						lovMap.put(Integer.valueOf(i), lov);
					}else{
						readMap.put(Integer.valueOf(i), "W");
					}
					indexList.add(Integer.valueOf(i));
				}
			}

			List<Object[]> optionList = this.listBySql("SELECT c.lov,v.lov_code,v.lov_desc FROM CUX_PO_MAP_DATA_COLS c,CUX_MD_LOV_VALUES v "+
					"WHERE c.CATEGORY = '"+masterType+"' AND c.LANGUAGE = '"+language+"' AND c.IS_DISPLAY = 'Y' AND c.ENABLED_FLAG = 'Y' and c.LOV is not null and c.lov=v.lov_type and v.language='"+language+"' and v.enabled_flag='Y' order by c.lov,v.lov_code desc");
			Map<String,String> optionMap=new HashMap<String,String>();
			if (optionList!=null && optionList.size()>0) {
				for (Object[] objects : optionList) {
					String lov=(String) objects[0];
					String lovCode=(String) objects[1];
					String lovDesc=(String) objects[2];
					optionMap.put(lov+"&"+lovDesc, lovCode);
				}
			}
			List<List<String>> insertdataList=new ArrayList<List<String>>();
			for (int i = 4; i < rowNum; i++) {
				Row row = sheet.getRow(i);
				if (row==null) {
					continue;
				}
				List<String> insertdata=new ArrayList<String>();
				for (Integer index : indexList) {
					String value = ExcelUtil.getCellStringValue(row.getCell(index),i);
					if ("WS".equals(readMap.get(index)) && StringUtils.isNotEmpty(value)) {
						value=optionMap.get(lovMap.get(index)+"&"+value);
						Assert.hasText(value, instrumentClassService.getLanguage(locale,"後臺配置已更新，請重新下載","The background configuration has changed,please download again"));
					}
					value = value.replaceAll("'","''");
					insertdata.add(value.trim());
				}
				if(!insertdata.isEmpty()&&!"".equals(insertdata.get(0))){
					insertdataList.add(insertdata);
				}
			}
			insertdataList = insertdataList.stream().distinct().collect(Collectors.toList());
			if (!insertdataList.isEmpty()) {
				this.saveBatch(tableNAME,columnList,insertdataList);
			}else{
				result.put("flag", "fail");
				result.put("msg", "無有效數據行");
			}
		} else {
			result.put("flag", "fail");
			result.put("msg", "對不起，未接受到上傳的文件");
		}
		return result.getJson();
	}

	/**單個新增營收映射表信息**/
	public void insert(String formVal,String type){
		String sql="insert into ";
		String val="";
		if("Account,CUX_RT_ACCOUNT_MAPPING".equals(type)){
			sql+=" CUX_RT_ACCOUNT_MAPPING(";
			String[] params = formVal.split("&");
			for (String param : params) {
				String columnName = param.substring(0,param.indexOf("="));
				String columnValue = param.substring(param.indexOf("=")+1).trim();
				if(columnName.equals("SALES_AREA")){
					String delete="delete from CUX_RT_ACCOUNT_MAPPING where SALES_AREA = ('"+columnValue+"')";
					userDao.getSessionFactory().getCurrentSession().createSQLQuery(delete).executeUpdate();
				}
				sql+=columnName+",";
				val+="'"+columnValue+"',";
			}
			sql=sql+"LAST_UPDATED_BY,ID ) values("+val+"'"+SecurityUtils.getLoginUsername()+"','"+ UUID.randomUUID()+"')";
			userDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		}else if("Sales_Account,CUX_RT_SALES_ACCOUNT_MAPPING".equals(type)){
			sql+=" CUX_RT_SALES_ACCOUNT_MAPPING(";
			String[] params = formVal.split("&");
			for (String param : params) {
				String columnName = param.substring(0,param.indexOf("="));
				String columnValue = param.substring(param.indexOf("=")+1).trim();
				if(columnName.equals("SALES_AREA")){
					String delete="delete from CUX_RT_ACCOUNT_MAPPING where SALES_AREA = ('"+columnValue+"')";
					userDao.getSessionFactory().getCurrentSession().createSQLQuery(delete).executeUpdate();
				}
				sql+=columnName+",";
				val+="'"+columnValue+"',";
			}
			sql=sql+"LAST_UPDATED_BY,ID ) values("+val+"'"+SecurityUtils.getLoginUsername()+"','"+ UUID.randomUUID()+"')";
			userDao.getSessionFactory().getCurrentSession().createSQLQuery(sql).executeUpdate();
		}
	}

	/**
	 * 營收下载
	 */
	public AjaxResult download(String masterData,String queryCondition,HttpServletRequest request,AjaxResult result) throws IOException {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String masterType=masterData.split(",")[0];
		String tableName=masterData.split(",")[1];
		String language=instrumentClassService.getLanguage(locale,"CN","EN");
		List<Object[]> titleList = this.listBySql("SELECT COL_NAME,COL_DESC,READ_WRITE,LOV FROM CUX_PO_MAP_DATA_COLS WHERE CATEGORY = '"+masterType+"' AND LANGUAGE = '"+language+"' AND IS_DISPLAY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)");
		Map<Integer,String> readMap=new HashMap<Integer, String>();
		Map<Integer,String> lovMap=new HashMap<Integer, String>();
		List<String> columnList=new ArrayList<String>();
		List<String> columnDescList=new ArrayList<String>();
		List<String> readList=new ArrayList<String>();
		List<String> lovList=new ArrayList<String>();
		String sql="select id,";
		for (int i = 0; i < titleList.size(); i++) {
			Object[] titleObjects=titleList.get(i);
			Object column = titleObjects[0];
			Object columnDesc = titleObjects[1];
			Object read = titleObjects[2];
			Object lov = titleObjects[3];
			String columnStr=(column==null?"":column.toString());
			String columnDescStr=(columnDesc==null?"":columnDesc.toString());
			String readStr=(read==null?"":read.toString());
			String lovStr=(lov==null?"":lov.toString());
			readMap.put(i+1, readStr);
			lovMap.put(i+1, lovStr);
			columnList.add(columnStr);
			columnDescList.add(columnDescStr);
			readList.add(readStr);
			lovList.add(lovStr);
			sql+=column+",";
		}
		sql=sql.substring(0, sql.length()-1)+" from "+tableName;
		if (StringUtils.isNotEmpty(queryCondition)) {
			sql+=" where 1=1 ";
			String[] params = queryCondition.split("&");
			for (String param : params) {
				String columnName = param.substring(0,param.indexOf("="));
				String columnValue = param.substring(param.indexOf("=")+1).trim();
				if (StringUtils.isNotEmpty(columnValue)) {
					columnValue=URLDecoder.decode(columnValue, "UTF-8");
					sql+=" and "+columnName+" like '%"+columnValue+"%'";
				}
			}
		}
		String optionSql="SELECT c.lov,v.lov_code,v.lov_desc FROM CUX_PO_MAP_DATA_COLS c,CUX_MD_LOV_VALUES v "+
				"WHERE c.CATEGORY = '"+masterType+"' AND c.LANGUAGE = '"+language+"' AND c.IS_DISPLAY = 'Y' AND c.ENABLED_FLAG = 'Y' and c.LOV is not null and c.lov=v.lov_type and v.language='"+language+"' and v.enabled_flag='Y' order by c.lov,v.lov_code desc";
		List<Object[]> optionList = this.listBySql(optionSql);
		Map<String,String> optionMap=new HashMap<String,String>();
		Map<String,List<String>> selectMap=new HashMap<String, List<String>>();
		if (optionList!=null && optionList.size()>0) {
			for (Object[] objects : optionList) {
				String lov=(String) objects[0];
				String lovCode=(String) objects[1];
				String lovDesc=(String) objects[2];
				optionMap.put(lov+"&"+lovCode, lovDesc);

				List<String> lovDescList = selectMap.get(lov);
				if (lovDescList==null) {
					lovDescList=new ArrayList<String>();
				}
				lovDescList.add(lovDesc);
				selectMap.put(lov, lovDescList);
			}
		}
		List<Object[]> dataList = this.listBySql(sql+" order by ID");
		String realPath = request.getRealPath("");
		File file=new File(request.getRealPath("")+File.separator+"static"+File.separator+"template"+File.separator+"admin"+File.separator+"營收映射表信息.xlsx");
		XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
		XSSFCellStyle titleStyle = workBook.createCellStyle();
		titleStyle.setLocked(true);
		titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
		XSSFFont font = workBook.createFont();
		font.setColor(IndexedColors.WHITE.index);
		font.setBold(true);
		titleStyle.setFont(font);

		XSSFCellStyle lockStyle = workBook.createCellStyle();
		lockStyle.setLocked(true);
		lockStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 247, 251)));
		lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFCellStyle unlockStyle = workBook.createCellStyle();
		unlockStyle.setLocked(false);
		unlockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		unlockStyle.setFillForegroundColor(IndexedColors.WHITE.index);

		SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
		String sheetName="";
		if("CUX_RT_ACCOUNT_MAPPING".equalsIgnoreCase(tableName)){
			sheetName=instrumentClassService.getLanguage(locale,"Account Mgr主數據表","Account Mgr");
		}else if("CUX_RT_SALES_ACCOUNT_MAPPING".equalsIgnoreCase(tableName)){
			sheetName=instrumentClassService.getLanguage(locale,"接口平臺Sales對應Account org表","Sales Corresponding Account org");
		}
		sxssfWorkbook.setSheetName(0, sheetName);
		Sheet sheet = sxssfWorkbook.getSheetAt(0);
		sheet.setColumnHidden(0, true);
		Row columnRow = sheet.createRow(0);
		Cell columnCell = columnRow.createCell(0);
		columnCell.setCellValue(language);
		columnCell.setCellStyle(lockStyle);
		for (int i = 0; i < columnList.size(); i++) {
			String column = columnList.get(i);
			Cell cell = columnRow.createCell(i+1);
			cell.setCellValue(column);
			cell.setCellStyle(lockStyle);
		}
		columnRow.setZeroHeight(true);

		Row readRow = sheet.createRow(1);
		Cell readCell = readRow.createCell(0);
		readCell.setCellValue(tableName);
		readCell.setCellStyle(lockStyle);
		for (int i = 0; i < readList.size(); i++) {
			String read = readList.get(i);
			Cell cell = readRow.createCell(i+1);
			cell.setCellValue(read);
			cell.setCellStyle(lockStyle);
		}
		readRow.setZeroHeight(true);

		Row lovRow = sheet.createRow(2);
		Cell lovCell = lovRow.createCell(0);
		lovCell.setCellValue("");
		lovCell.setCellStyle(lockStyle);
		for (int i = 0; i < lovList.size(); i++) {
			String lov = lovList.get(i);
			Cell cell = lovRow.createCell(i+1);
			cell.setCellValue(lov);
			cell.setCellStyle(lockStyle);
		}
		lovRow.setZeroHeight(true);

		Row columnDescRow = sheet.createRow(3);
		for (int i = 0; i < columnDescList.size(); i++) {
			String columnDesc = columnDescList.get(i);
			Cell cell = columnDescRow.createCell(i+1);
			cell.setCellValue(columnDesc);
			cell.setCellStyle(titleStyle);
		}
		if (null !=dataList && !dataList.isEmpty() && dataList.size()>0) {
			for (int j = 1; j < dataList.get(0).length; j++) {
				if ("W".equals(readMap.get(Integer.valueOf(j)))) {
					String lov = lovMap.get(Integer.valueOf(j));
					if (StringUtils.isNotEmpty(lov)) {
						List<String> lovDescList = selectMap.get(lov);
						if (lovDescList != null && lovDescList.size() > 0) {
							String[] subjects = new String[lovDescList.size()];
							lovDescList.toArray(subjects);
							DataValidationHelper helper = sheet.getDataValidationHelper();
							DataValidationConstraint constraint = helper.createExplicitListConstraint(subjects);
							CellRangeAddressList addressList = new CellRangeAddressList(4, dataList.size() + 4, j, j);
							DataValidation dataValidation = helper.createValidation(constraint, addressList);
							sheet.addValidationData(dataValidation);
						}
					}
				}
			}

			for (int i = 0; i < dataList.size(); i++) {
				Object[] objects = dataList.get(i);
				String id = objects[0].toString();
				Row row = sheet.createRow(i + 4);
				StringBuffer key = new StringBuffer();
				int j = 0;
				for (; j < objects.length; j++) {
					int a = j;
					Cell cell = row.createCell(a);
					Object obj = objects[j];
					String value = (obj == null ? "" : obj.toString());
					if ("W".equals(readMap.get(Integer.valueOf(j)))) {
						cell.setCellStyle(unlockStyle);
						String lov = lovMap.get(Integer.valueOf(j));
						if (StringUtils.isNotEmpty(lov)) {
							if (StringUtils.isNotEmpty(value)) {
								value = optionMap.get(lov + "&" + value);
							}
						}
						cell.setCellValue(value);
						key.append(value);
					} else {
						cell.setCellStyle(lockStyle);
						cell.setCellValue(value);
					}
				}
				String base64 = Base64.getEncoder().encodeToString(key.toString().getBytes());
				Cell idCell = row.createCell(0);
				idCell.setCellValue(id + "," + base64);
				idCell.setCellStyle(lockStyle);
			}
		}
		File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+System.currentTimeMillis()+".xlsx");
		OutputStream out = new FileOutputStream(outFile);
		sxssfWorkbook.write(out);
		sxssfWorkbook.close();
		out.flush();
		out.close();
		result.put("fileName", outFile.getName());
		result.put("templateName", sheetName+".xlsx");
		return result;
	}
	/**
	 * 采購下载
	 */
	public AjaxResult downloadPo(String masterData,String queryCondition,HttpServletRequest request,AjaxResult result) throws IOException{
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String masterType=masterData.split(",")[0];
		String tableName=masterData.split(",")[1];
		if("CUX_SBU_MAPPING".equalsIgnoreCase(tableName)){
			tableName="bidev.v_if_sbu_mapping";
		}
		String language=instrumentClassService.getLanguage(locale,"CN","EN");
		List<Object[]> titleList = this.listBySql("SELECT COL_NAME,COL_DESC,READ_WRITE,LOV FROM CUX_PO_MAP_DATA_COLS WHERE CATEGORY = '"+masterType+"' AND LANGUAGE = '"+language+"' AND IS_DISPLAY = 'Y' AND ENABLED_FLAG = 'Y' ORDER BY to_number(COL_SEQ)");
		Map<Integer,String> readMap=new HashMap<Integer, String>();
		Map<Integer,String> lovMap=new HashMap<Integer, String>();
		List<String> columnList=new ArrayList<String>();
		List<String> columnDescList=new ArrayList<String>();
		List<String> readList=new ArrayList<String>();
		List<String> lovList=new ArrayList<String>();
		String sql="select id,";
		if("bidev.v_if_sbu_mapping".equalsIgnoreCase(tableName)){
			sql="select ID_SBU,";
		}
		for (int i = 0; i < titleList.size(); i++) {
			Object[] titleObjects=titleList.get(i);
			Object column = titleObjects[0];
			Object columnDesc = titleObjects[1];
			Object read = titleObjects[2];
			Object lov = titleObjects[3];
			String columnStr=(column==null?"":column.toString());
			String columnDescStr=(columnDesc==null?"":columnDesc.toString());
			String readStr=(read==null?"":read.toString());
			String lovStr=(lov==null?"":lov.toString());

			readMap.put(i+1, readStr);
			lovMap.put(i+1, lovStr);

			columnList.add(columnStr);
			columnDescList.add(columnDescStr);
			readList.add(readStr);
			lovList.add(lovStr);

			sql+=column+",";
		}
		sql=sql.substring(0, sql.length()-1);
		sql+=" from "+tableName;
		if (StringUtils.isNotEmpty(queryCondition)) {
			sql+=" where 1=1 ";
			String[] params = queryCondition.split("&");
			for (String param : params) {
				String columnName = param.substring(0,param.indexOf("="));
				String columnValue = param.substring(param.indexOf("=")+1).trim();
				if (StringUtils.isNotEmpty(columnValue)) {
					columnValue=URLDecoder.decode(columnValue, "UTF-8");
					sql+=" and "+columnName+" like '%"+columnValue+"%'";
				}
			}
			if(!"bidev.v_if_sbu_mapping".equalsIgnoreCase(tableName)){
				sql+=" order by ID";
			}else{
				sql+= " and NEW_SBU_NAME in('IDS','EMS','ABS','ACE','ASD','AEC','TSC','APS','CW','FAD','IoT','CIDA','Tengyang','TMTS','FIAD')";
			}
		}
		String optionSql="SELECT c.lov,v.lov_code,v.lov_desc FROM CUX_PO_MAP_DATA_COLS c,CUX_MD_LOV_VALUES v "+
				"WHERE c.CATEGORY = '"+masterType+"' AND c.LANGUAGE = '"+language+"' AND c.IS_DISPLAY = 'Y' AND c.ENABLED_FLAG = 'Y' and c.LOV is not null and c.lov=v.lov_type and v.language='"+language+"' and v.enabled_flag='Y' order by c.lov,v.lov_code desc";
		List<Object[]> optionList = this.listBySql(optionSql);

		Map<String,String> optionMap=new HashMap<String,String>();
		Map<String,List<String>> selectMap=new HashMap<String, List<String>>();
		if (optionList!=null && optionList.size()>0) {
			for (Object[] objects : optionList) {
				String lov=(String) objects[0];
				String lovCode=(String) objects[1];
				String lovDesc=(String) objects[2];
				optionMap.put(lov+"&"+lovCode, lovDesc);

				List<String> lovDescList = selectMap.get(lov);
				if (lovDescList==null) {
					lovDescList=new ArrayList<String>();
				}
				lovDescList.add(lovDesc);
				selectMap.put(lov, lovDescList);
			}
		}
		List<Object[]> dataList = this.listBySql(sql);
		if (dataList.isEmpty()) {
			result.put("flag", "fail");
			result.put("msg", instrumentClassService.getLanguage(locale,"没有查询到可下载的数据","No data found"));
			return result;
		}

		String realPath = request.getRealPath("");
		File file=new File(request.getRealPath("")+File.separator+"static"+File.separator+"template"+File.separator+"admin"+File.separator+"採購映射表信息.xlsx");
		XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
		XSSFCellStyle titleStyle = workBook.createCellStyle();
		titleStyle.setLocked(true);
		titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		titleStyle.setFillForegroundColor(IndexedColors.BLACK.index);
		XSSFFont font = workBook.createFont();
		font.setColor(IndexedColors.WHITE.index);
		font.setBold(true);
		titleStyle.setFont(font);

		XSSFCellStyle lockStyle = workBook.createCellStyle();
		lockStyle.setLocked(true);
		lockStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 247, 251)));
		lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFCellStyle unlockStyle = workBook.createCellStyle();
		unlockStyle.setLocked(false);
		unlockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		unlockStyle.setFillForegroundColor(IndexedColors.WHITE.index);

		SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
		String sheetName=instrumentClassService.getLanguage(locale,"採購映射表","PO Mapping");
		sxssfWorkbook.setSheetName(0, sheetName);
		Sheet sheet = sxssfWorkbook.getSheetAt(0);
		sheet.setColumnHidden(0, true);
		Row columnRow = sheet.createRow(0);
		Cell columnCell = columnRow.createCell(0);
		columnCell.setCellValue(language);
		columnCell.setCellStyle(lockStyle);
		for (int i = 0; i < columnList.size(); i++) {
			String column = columnList.get(i);
			Cell cell = columnRow.createCell(i+1);
			cell.setCellValue(column);
			cell.setCellStyle(lockStyle);
		}
		columnRow.setZeroHeight(true);

		Row readRow = sheet.createRow(1);
		Cell readCell = readRow.createCell(0);
		readCell.setCellValue(tableName);
		readCell.setCellStyle(lockStyle);
		for (int i = 0; i < readList.size(); i++) {
			String read = readList.get(i);
			Cell cell = readRow.createCell(i+1);
			cell.setCellValue(read);
			cell.setCellStyle(lockStyle);
		}
		readRow.setZeroHeight(true);

		Row lovRow = sheet.createRow(2);
		Cell lovCell = lovRow.createCell(0);
		lovCell.setCellValue("");
		lovCell.setCellStyle(lockStyle);
		for (int i = 0; i < lovList.size(); i++) {
			String lov = lovList.get(i);
			Cell cell = lovRow.createCell(i+1);
			cell.setCellValue(lov);
			cell.setCellStyle(lockStyle);
		}
		lovRow.setZeroHeight(true);

		Row columnDescRow = sheet.createRow(3);
		for (int i = 0; i < columnDescList.size(); i++) {
			String columnDesc = columnDescList.get(i);
			Cell cell = columnDescRow.createCell(i+1);
			cell.setCellValue(columnDesc);
			cell.setCellStyle(titleStyle);
		}

		for (int j = 1; j < dataList.get(0).length; j++) {
			if ("W".equals(readMap.get(Integer.valueOf(j)))) {
				String lov = lovMap.get(Integer.valueOf(j));
				if (StringUtils.isNotEmpty(lov)) {
					List<String> lovDescList = selectMap.get(lov);
					if (lovDescList!=null && lovDescList.size()>0) {
						String [] subjects = new String[lovDescList.size()];
						lovDescList.toArray(subjects);
						DataValidationHelper helper = sheet.getDataValidationHelper();
						DataValidationConstraint constraint = helper.createExplicitListConstraint(subjects);
						CellRangeAddressList addressList = new CellRangeAddressList(4, dataList.size()+4, j, j);
						DataValidation dataValidation = helper.createValidation(constraint, addressList);
						sheet.addValidationData(dataValidation);
					}
				}
			}
		}

		for (int i = 0; i < dataList.size(); i++) {
			Object[] objects=dataList.get(i);
			String id = objects[0].toString();
			Row row = sheet.createRow(i+4);
			StringBuffer key=new StringBuffer();
			int j=0;
			if(!"EPMEBS.CUX_SBU_BU_MAPPING".equalsIgnoreCase(tableName)){
				j=1;
			}
			for (; j < objects.length; j++) {
				int a=j;
				if("EPMEBS.CUX_SBU_BU_MAPPING".equalsIgnoreCase(tableName)){
					a=a+1;
				}
				Cell cell = row.createCell(a);
				Object obj=objects[j];
				String value=(obj==null?"":obj.toString());
				if ("W".equals(readMap.get(Integer.valueOf(j)))) {
					cell.setCellStyle(unlockStyle);
					String lov = lovMap.get(Integer.valueOf(j));
					if (StringUtils.isNotEmpty(lov)) {
						if (StringUtils.isNotEmpty(value)) {
							value=optionMap.get(lov+"&"+value);
						}
					}
					cell.setCellValue(value);
					key.append(value);
				}else{
					cell.setCellStyle(lockStyle);
					cell.setCellValue(value);
				}
			}
			String base64 = Base64.getEncoder().encodeToString(key.toString().getBytes());
			Cell idCell = row.createCell(0);
			idCell.setCellValue(id+","+base64);
			idCell.setCellStyle(lockStyle);
		}

		File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+System.currentTimeMillis()+".xlsx");
		OutputStream out = new FileOutputStream(outFile);
		sxssfWorkbook.write(out);
		sxssfWorkbook.close();
		out.flush();
		out.close();

		result.put("fileName", outFile.getName());
		result.put("templateName", sheetName+".xlsx");
		return result;
	}
	/**
	 * 刪除
	 */
	public void delete( String masterData,String updateData){
		String tableName=masterData.split(",")[1];
		if (StringUtils.isNotEmpty(updateData)) {
			String deleteSql="delete from "+tableName;
			String where="";
			String[] params = updateData.split("&");

			String columnName = params[0].substring(0, params[0].indexOf("="));
			String columnValue =  params[0].substring( params[0].indexOf("=")+1).trim();
			if ("ID".equalsIgnoreCase(columnName)) {
				where=" where ID='"+columnValue+"'";
			}
			deleteSql+=where;
			this.updateMasterData(deleteSql);
		}
	}

	public void updateMasterData(String updateSql) {
		userDao.getSessionFactory().getCurrentSession().createSQLQuery(updateSql).executeUpdate();
	}

	/**采購映射表更新**/
	public void updatePo(String masterData,String updateData){
		String tableName=masterData.split(",")[1];
		if (StringUtils.isNotEmpty(updateData)) {
			String updateSql="update "+tableName+" set ";
			String where="";
			String[] params = updateData.split("&");
			if("ID".equals(params[0].substring(0,params[0].indexOf("=")))
					&&"addData".equals(params[0].substring(params[0].indexOf("=")+1).trim())){
				UserDetailImpl loginUser = SecurityUtils.getLoginUser();
				updateSql="";
				for (String param : params) {
					String columnName = param.substring(0,param.indexOf("="));
					String columnValue = param.substring(param.indexOf("=")+1).trim();
					updateSql+=columnName+",";
					if("addData".equals(columnValue)){
						where+="'"+UUID.randomUUID()+"',";
					}else{
						where+="'"+columnValue+"',";
					}
				}
				updateSql="insert into "+tableName+"("+updateSql+"LAST_UPDATED_BY,CREATED_BY) values("
						+where+"'"+loginUser.getUsername()+"','"+loginUser.getUsername()+"')";
			}else{
				for (String param : params) {
					String columnName = param.substring(0,param.indexOf("="));
					String columnValue = param.substring(param.indexOf("=")+1).trim();
					if ("ID".equalsIgnoreCase(columnName)) {
						where=" where ID='"+columnValue+"'";
					}else{
						if (StringUtils.isNotEmpty(columnValue)) {
							updateSql+=columnName+"='"+columnValue+"',";
						}
					}
				}
				updateSql=updateSql+" LAST_UPDATED_DATE=sysdate,LAST_UPDATED_BY='"+ SecurityUtils.getLoginUsername()+"'";
				updateSql+=where;
			}
			this.updateMasterData(updateSql);
		}
	}
}
