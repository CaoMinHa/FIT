package foxconn.fit.controller.bi;

import com.alibaba.fastjson.JSONObject;
import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.PoColumns;
import foxconn.fit.entity.bi.PoTable;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.bi.InstrumentClassService;
import foxconn.fit.service.bi.PoTableService;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@Controller
@RequestMapping("/bi/rgpIntegration")
public class RgpIntegrationController extends BaseController {
    @Autowired
    private PoTableService poTableService;
    @Autowired
    private InstrumentClassService instrumentClassService;

    @RequestMapping(value = "index")
    public String index(PageRequest pageRequest, Model model, HttpServletRequest request) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            UserDetailImpl loginUser = SecurityUtils.getLoginUser();
            String userName=loginUser.getUsername();
            String sql="select TABLE_LIST from fit_user where USERNAME='"+userName+"'";
            List<String> selectTable= poTableService.listBySql(sql);
            String tableListValue="";
            if(null!=selectTable&&selectTable.size()>0){
                if(null!=selectTable.get(0)){
                    String[] ids = selectTable.get(0).split(",");
                    tableListValue="and TABLE_NAME in(";
                    for (String s : ids) {
                        tableListValue=tableListValue+"'"+s+"',";
                    }
                    tableListValue=tableListValue.substring(0,tableListValue.length()-1);
                    tableListValue+=")";
                }
            }

            String uploadSql = "select * from Fit_po_table where TYPE='RGP' "+tableListValue+" order by serial";  //Upload_flag='Y'
            List<PoTable> poTableList = poTableService.listBySql(uploadSql, PoTable.class);
            List<PoTable> tableList = new ArrayList<PoTable>();
            for (PoTable poTable : poTableList) {
                tableList.add(new PoTable(poTable.getTableName(),getByLocale(locale, poTable.getComments()),poTable.getUploadFlag()));
            }
            model.addAttribute("poTableList", tableList);
        } catch (Exception e) {
            logger.error("查询明细配置表列表信息失败", e);
        }
        return "/bi/rgpIntegration/index";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, PageRequest pageRequest, HttpServletRequest request, String tableName,String queryCondition) {
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            PoTable poTable = poTableService.get(tableName);
            List<PoColumns> columns = poTable.getColumns();
            for (PoColumns poColumns : columns) {
                poColumns.setComments(getByLocale(locale, poColumns.getComments()));
            }
            String sql=this.querySql(columns,queryCondition,poTable.getTableName());
            if("CUX_RGP_SCRAPS_APPORTION".equalsIgnoreCase(poTable.getTableName()) || "IF_EBS_AR_REVENUE_DTL_SCRAP".equalsIgnoreCase(poTable.getTableName())){
                sql+=" and sbu in("+selectSBU()+")";
            }else{
                sql+=" and BM_SBU in("+this.selectBMSBU()+")";
            }
            Page<Object[]> page = poTableService.findPageBySql(pageRequest, sql);
            int index = 1;
            if (pageRequest.getPageNo() > 1) {
                index = 2;
            }
            if("N".equals(poTable.getUploadFlag())){
                model.addAttribute("hidden", 1);
            }else{
                model.addAttribute("hidden", 2);
            }
            model.addAttribute("index", index);
            model.addAttribute("tableName", poTable.getTableName());
            model.addAttribute("page", page);
            model.addAttribute("columns", columns);
        } catch (Exception e) {
            logger.error("查询明细配置表列表失败:", e);
        }
        return "/bi/rgpIntegration/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "营收模块-->上传")
    public String upload(HttpServletRequest request, HttpServletResponse response, AjaxResult result,
                         @Log(name = "年月") String date, @Log(name = "明细表名称") String[] tableNames) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        result.put("msg", getLanguage(locale, "上傳成功", "Upload success"));
        try {
            Assert.isTrue(tableNames != null && tableNames.length > 0, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mutipartFiles = multipartHttpServletRequest.getFileMap();
            Map<PoTable, List<List<String>>> dataMap = new HashMap<PoTable, List<List<String>>>();
            if (mutipartFiles != null && mutipartFiles.size() > 0) {
                //文件基础格式校验
                result=this.uploadExcelCheck(mutipartFiles,request,result,tableNames);
                Map<Object,Object> map =result.getResult();
                if(map.get("flag").equals("success")){
                    for (int i = 0; i < tableNames.length; i++) {
                        String tableName = tableNames[i];
                        PoTable poTable = poTableService.get(tableName);
                        List<List<String>> dataList= (List<List<String>>) map.get(tableName);
                        if (!dataList.isEmpty()) {
                            //上传数据校验
                        List<String> msg=outSourcingMaterialCheck(dataList);
                            if(!"成功".equals(msg.get(0))){
                                result.put("flag", "fail");
                                result.put("msg", getLanguage(locale, msg.get(0),msg.get(1)));
                                return result.getJson();
                            }else{
                                dataMap.put(poTable, dataList);
                            }
                        } else {
                            result.put("flag", "fail");
                            result.put("msg", getLanguage(locale, "第" + (i + 1) + "個sheet無有效數據行", "The sheet " + (i + 1) + " has no valid data row"));
                            return result.getJson();
                        }
                    }
                    poTableService.saveData(dataMap);
                }else{
                    return result.getJson();
                }
            } else {
                result.put("flag", "fail");
                result.put("msg", getLanguage(locale, "對不起，未接收到上傳的文件", "Uploaded file not received"));
            }
        } catch (Exception e) {
            logger.error("保存文件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }


    public  List<String> outSourcingMaterialCheck(List<List<String>> list){
        List msg=new ArrayList();
        List partNo=new ArrayList();
        List bmSbu=new ArrayList();
        for (List<String> s : list) {
            bmSbu.add(s.get(0));
            partNo.add(s.get(1));
            partNo.add(s.get(2));
        }
        partNo= instrumentClassService.removeDuplicate(partNo);
        String p=" and segment1 in (";
        String sbuSql="SELECT distinct segment1 FROM apps.mtl_system_items_b@epmtebs where 1=1 ";
        for (int i=0;i<partNo.size();i++){
            p+="'"+partNo.get(i)+"',";
            if(i%900==0){
                sbuSql+=p.substring(0,p.length()-1)+")";
                p=" or segment1  in (";
            }
        }
        sbuSql+=p.substring(0,p.length()-1)+")";
        bmSbu= instrumentClassService.removeDuplicate(bmSbu);
        String bmsbus=this.selectBMSBU();
        List<String> partNoCount= instrumentClassService.removeDuplicate(poTableService.listBySql(sbuSql));
        String val="";
        for (int i=0;i<bmSbu.size();i++) {
            if(!bmsbus.contains("'"+bmSbu.get(i)+"'")){
                val+=bmSbu.get(i)+",";
            }
        }
        if(partNoCount.size()!=partNo.size()){
            msg.add("("+ instrumentClassService.getDiffrent(partNo,partNoCount)+")料號在EBS系統無效，請檢查。");
            msg.add("("+ instrumentClassService.getDiffrent(partNo,partNoCount)+")Item number is invalid in EBS.");
            return msg;
        } else if(null!=val&&!val.equals("")){
            msg.add("("+val.substring(0,val.length()-1)+")SBU沒有權限，請維護。");
            msg.add("("+val.substring(0,val.length()-1)+")SBU do not have permission,Please maintain.");
            return msg;
        }
        msg.add("成功");
        return msg;
    }


    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "採購模块-->下载")
    public synchronized String download(HttpServletRequest request, HttpServletResponse response, PageRequest pageRequest, AjaxResult result,
                                         @Log(name = "公司编码") String entity, @Log(name = "明细表名称") String tableNames,
            @Log(name = "查询条件") String queryCondition,String checkedVal) {
        OutputStream out=null;
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableNames, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            XSSFWorkbook workBook = new XSSFWorkbook();
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workBook);
            //生成下载Excel
            String special="";
            if("CUX_RGP_SCRAPS_APPORTION".equalsIgnoreCase(tableNames) || "IF_EBS_AR_REVENUE_DTL_SCRAP".equalsIgnoreCase(tableNames)){
                special+=" and sbu in("+selectSBU()+")";
            }else{
                special+=" and BM_SBU in("+this.selectBMSBU()+")";
            }
            this.downloadExcel(checkedVal,tableNames,locale,sxssfWorkbook,workBook,queryCondition,pageRequest,special);
            String fileName = tableNames;
            String tableNameSql = "select * from fit_po_table";
            List<PoTable> list = poTableService.listBySql(tableNameSql, PoTable.class);
            for (int i = 0; i < list.size(); i++) {
                if (fileName.equalsIgnoreCase(list.get(i).getTableName())) {
                    fileName = list.get(i).getComments().split("_")[1];
                    break;
                }
            }
            File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download" + File.separator + fileName + ".xlsx");
            out = new FileOutputStream(outFile);
            sxssfWorkbook.write(out);
            sxssfWorkbook.close();
            result.put("fileName", outFile.getName());
            System.gc();
        } catch (Exception e) {
            logger.error("下载Excel失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }finally {
            try {
                out.flush();
                out.close();
            }catch (Exception e){
                e.printStackTrace();;
            }
        }
        return result.getJson();
    }

    /**
     * 下載模板
     *
     * @param request
     * @param response
     * @param pageRequest
     * @param result
     * @param tableNames
     * @param table_type
     * @return
     */

    @RequestMapping(value = "template")
    @ResponseBody
    public synchronized String template(HttpServletRequest request, HttpServletResponse response, PageRequest pageRequest, AjaxResult result, String tableNames, String table_type) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        OutputStream out=null;
        try {
            Assert.hasText(tableNames, getLanguage(locale, "明細表不能為空", "The table cannot be empty"));
            if (tableNames.endsWith(",")) {
                tableNames = tableNames.substring(0, tableNames.length() - 1);
            }
            XSSFWorkbook workBook = new XSSFWorkbook();
            //单元列锁定
            XSSFCellStyle lockStyle = workBook.createCellStyle();
            lockStyle.setLocked(true);
            lockStyle.setAlignment(HorizontalAlignment.CENTER);
            lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            lockStyle.setFillForegroundColor(IndexedColors.WHITE.index);
            lockStyle.setBorderBottom(BorderStyle.THIN);
            lockStyle.setBorderRight(BorderStyle.THIN);
            lockStyle.setBorderLeft(BorderStyle.THIN);
            lockStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM,new XSSFColor(new java.awt.Color(212, 212, 212)));
            lockStyle.setBorderColor(XSSFCellBorder.BorderSide.RIGHT,new XSSFColor(new java.awt.Color(212, 212, 212)));
            lockStyle.setBorderColor(XSSFCellBorder.BorderSide.LEFT,new XSSFColor(new java.awt.Color(212, 212, 212)));
            //	单元格不锁定的样式
            XSSFCellStyle unlockstyle = workBook.createCellStyle();
            unlockstyle.setLocked(false);

            String fileName =tableNames;
            PoTable poTable = poTableService.get(tableNames);
            List<PoColumns> columns = poTable.getColumns();
            XSSFSheet sheet = workBook.createSheet(getByLocale(locale, poTable.getComments()));
            //处理基础表样
            this.getSheet(sheet,columns,locale,workBook);
            //统一先去掉表锁定
            for (short i = 0; i < columns.size();i++) {
                sheet.setDefaultColumnStyle(i, unlockstyle);
            }
            //根据字段锁定对应列
            //外购原料输入表
//            if ("CUX_RGP_OUTSOURCING_MATERIAL".equalsIgnoreCase(tableNames)) {
//                sheet.setDefaultColumnStyle(7,lockStyle);
//                sheet.setDefaultColumnStyle(8,lockStyle);
//            }
            //獲取實際表名
            List<PoTable> list = poTableService.listBySql("select * from fit_po_table", PoTable.class);
            for (int i = 0; i < list.size(); i++) {
                if (fileName.equalsIgnoreCase(list.get(i).getTableName())) {
                    fileName = list.get(i).getComments().split("_")[1];
                    break;
                }
            }
            File outFile = new File(request.getRealPath("") + File.separator + "static" + File.separator + "download/" + fileName + ".xlsx");
            out = new FileOutputStream(outFile);
            workBook.write(out);
            workBook.close();
            result.put("fileName", outFile.getName());
        } catch (Exception e) {
            logger.error("下载模板文件失败", e);
            result.put("flag", "fail");
            result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + ExceptionUtil.getRootCauseMessage(e));
        }finally {
            try {
                out.flush();
                out.close();
            }catch (Exception e){
                e.printStackTrace();;
            }
        }
        return result.getJson();
    }



    @RequestMapping(value = "/delete")
    @ResponseBody
    public String deleteAll(AjaxResult ajaxResult, HttpServletRequest request, String id, String tableName) {
        try {
            String[] ids = id.split(",");
            String deleteSql = " delete from " + tableName + " where id in (";
            String whereSql = "";
            for (String s : ids) {
                whereSql = whereSql + "'" + s + "',";
            }
            whereSql = whereSql.substring(0, whereSql.length() - 1);
            deleteSql += whereSql + ")";
            poTableService.getDao().getSessionFactory().getCurrentSession().createSQLQuery(deleteSql).executeUpdate();
        } catch (Exception e) {
            logger.error("刪除" + tableName + "数据失败", e);
            ajaxResult.put("flag", "fail");
            ajaxResult.put("msg", "刪除数据失败(delete data Fail) : " + ExceptionUtil.getRootCauseMessage(e));
        }
        return ajaxResult.getJson();
    }



    @RequestMapping(value = "queryCondition")
    @ResponseBody
    public String queryMasterData(HttpServletRequest request,HttpServletResponse response,AjaxResult result,String tableName){
        try {
            Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            Assert.hasText(tableName, getLanguage(locale,"输入表不能为空","Master data can not be null"));
            String sql="SELECT COLUMN_NAME,COMMENTS FROM fit_po_table_columns WHERE  table_name='"+tableName+"'  AND IS_QUERY = 'Y'  ORDER BY to_number(SERIAL)";
            List<Map> list = poTableService.listMapBySql(sql);
            List<List<String>> a=new ArrayList<>();
            for (Map poColumns : list) {
                List<String> b=new ArrayList<>();
                b.add(poColumns.get("COLUMN_NAME").toString());
                b.add(getByLocale(locale, poColumns.get("COMMENTS").toString()));
                a.add(b);
            }
            result.put("queryList", a);
        } catch (Exception e) {
            logger.error("查询採購映射表信息失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }
        return result.getJson();
    }

}
