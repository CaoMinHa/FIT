package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.bi.ActualPoNPriceCDDetail;
import foxconn.fit.service.bi.ActualPoNPriceCDDetailService;
import foxconn.fit.util.DateUtil;
import foxconn.fit.util.ExcelUtil;
import foxconn.fit.util.ExceptionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bi/actualPoNPriceCD")
public class ActualPoNPriceCDDetailController extends BaseController {
    private static String EXCEL_NAME="實際採購非價格CD明細";
    private static int COLUMN_NUM=18;
    @Autowired
    private ActualPoNPriceCDDetailService actualPoNPriceCDDetailService;

    @RequestMapping(value = "index")
    public String index(Model model) {
        List<String> list = actualPoNPriceCDDetailService.listBySql("select distinct SBU from FIT_Revenue_Actual_Number order by SBU");
        model.addAttribute("codelist", list);
        return "/bi/actualPoNPriceCD/index";
    }

    @RequestMapping(value="/list")
    public String list(Model model, PageRequest pageRequest, String scenario,String vView, String period,String commodity) {
        try {
            List<PropertyFilter> filters = new ArrayList<PropertyFilter>();

            if (StringUtils.isNotEmpty(period)) {
                Date date = DateUtil.parseByYyyy_MM(period);
                Assert.notNull(date, "年月格式错误(Error Date Formats)");
                String[] split = period.split("-");
                String year = split[0];
                String month = split[1];
                filters.add(new PropertyFilter("EQS_year",year));
                filters.add(new PropertyFilter("EQS_period",month));
            }

            if (StringUtils.isNotEmpty(scenario)) {
                filters.add(new PropertyFilter("EQS_scenario",scenario));
            }

            if (StringUtils.isNotEmpty(vView)) {
                filters.add(new PropertyFilter("EQS_vView",vView));
            }
            if (StringUtils.isNotEmpty(commodity)) {
                filters.add(new PropertyFilter("EQS_commodity",commodity));
            }

            Page<ActualPoNPriceCDDetail> page = actualPoNPriceCDDetailService.findPageByHQL(pageRequest, filters);

            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("查询實際採購費價格CD明細表失败:", e);
        }
        return "/bi/actualPoNPriceCD/list";
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    @Log(name = "廠區營收收集-->上传")
    public String upload(HttpServletRequest request, HttpServletResponse response, AjaxResult result, @Log(name = "年月") String month) {
        result.put("msg", "上传成功(Upload Success)");
        try {
            Date date = DateUtil.parseByYyyy_MM(month);
            Assert.notNull(date, "年月格式错误(Error Date Formats)");

            String[] split = month.split("-");
            String year = split[0];
            String period = split[1];

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
                    result.put("msg", "请您上传正确格式的Excel文件(Error File Formats)");
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
                Row firstRow = sheet.getRow(1);
                Assert.notNull(firstRow, "第二行为标题行，不允许为空(Second Row Not Empty)");
                int column = firstRow.getPhysicalNumberOfCells();

                if(column<COLUMN_NUM){
                    result.put("flag", "fail");
                    result.put("msg", "Excel列数不能小于"+COLUMN_NUM+"(Number Of Columns Can Not Less Than"+COLUMN_NUM+")");
                    return result.getJson();
                }

                int rowNum = sheet.getPhysicalNumberOfRows();
                if (rowNum<3) {
                    result.put("flag", "fail");
                    result.put("msg", "检测到Excel没有行数据(Row Data Not Empty)");
                    return result.getJson();
                }

                List<ActualPoNPriceCDDetail> list=new ArrayList<ActualPoNPriceCDDetail>();

                for (int i = 2; i < rowNum; i++) {
                    Row row = sheet.getRow(i);

                    if (row==null) {
                        continue;
                    }

                    boolean isBlankRow=true;
                    for (int j = 0; j < COLUMN_NUM; j++) {
                        if (StringUtils.isNotEmpty(ExcelUtil.getCellStringValue(row.getCell(j),i))) {
                            isBlankRow=false;
                        }
                    }

                    if (isBlankRow) {
                        continue;
                    }

                    ActualPoNPriceCDDetail detail=null;

                    String tempYear = ExcelUtil.getCellStringValue(row.getCell(1),i);
                    String tempPeriod = ExcelUtil.getCellStringValue(row.getCell(2),i);

                    Date tempDate = DateUtil.parseByYyyy_MM(tempYear+"-"+tempPeriod);

                    Assert.notNull(tempDate, "第"+(i+1)+"行数据年或期间格式错误(The Date Of The "+(i+1)+"th Row Has Error Format)");
                    int n=3;
                    if (date.compareTo(tempDate)==0) {
                        detail=new ActualPoNPriceCDDetail();
                        detail.setScenario(ExcelUtil.getCellStringValue(row.getCell(0),i));
                        detail.setYear(year);
                        detail.setPeriod(period);
                        detail.setCommodity(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setvView(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setBu(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setSbu(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setLowerToleranceLimit(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setChangeItem(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setSecondSource(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setCompetor(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setPriceContinueLimit(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setLowerContact(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setRebate(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setPunitiveDeduction(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setFreeSample(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setSluggishTreatment(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                        detail.setTradeTerm(ExcelUtil.getCellStringValue(row.getCell(n++),i));
                    }

                    if (detail!=null) {
                        list.add(detail);
                    }
                }

                if (!list.isEmpty()) {
                    String codeCondition="where year='"+year+"' and period='"+period+"'";
                    actualPoNPriceCDDetailService.saveBatch(list, codeCondition);
                }else{
                    result.put("flag", "fail");
                    result.put("msg", "无有效数据行(Unreceived Valid Row Data)");
                }
            } else {
                result.put("flag", "fail");
                result.put("msg", "对不起,未接收到上传的文件(Unreceived File)");
            }
        } catch (Exception e) {
            logger.error("保存文件失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();
    }

    @RequestMapping(value = "download")
    @ResponseBody
    @Log(name = "廠區營收收集-->下载")
    public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,@Log(name = "年月") String month){
        try {
            Date date = DateUtil.parseByYyyy_MM(month);
            Assert.notNull(date, "年月格式错误(Error Date Formats)");
            String[] split = month.split("-");
            String year = split[0];
            String period = split[1];

            List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
            pageRequest.setOrderBy("year,period");
            pageRequest.setOrderDir(PageRequest.Sort.ASC+","+PageRequest.Sort.ASC);
            filters.add(new PropertyFilter("EQS_year",year));
            filters.add(new PropertyFilter("EQS_period",period));

            List<ActualPoNPriceCDDetail> list = actualPoNPriceCDDetailService.listByHQL(filters, pageRequest);

            String realPath = request.getRealPath("");
            if (CollectionUtils.isNotEmpty(list)) {
                long time = System.currentTimeMillis();
                File file=new File(realPath+File.separator+"static"+File.separator+"template"+File.separator+"bi"+File.separator+EXCEL_NAME+".xlsx");
                XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(file));
                XSSFCellStyle style = workBook.createCellStyle();
                style.setAlignment(HorizontalAlignment.CENTER);

                SXSSFWorkbook sxssfWorkbook=new SXSSFWorkbook(workBook);
                Sheet sheet = sxssfWorkbook.getSheetAt(0);
                for (int i = 0; i < list.size(); i++) {
                    ActualPoNPriceCDDetail detail = list.get(i);
                    Row row = sheet.createRow(i+1);
                    ExcelUtil.createCell(style, detail, row);
                }

                File outFile=new File(realPath+File.separator+"static"+File.separator+"download"+File.separator+time+".xlsx");
                OutputStream out = new FileOutputStream(outFile);
                sxssfWorkbook.write(out);
                sxssfWorkbook.close();
                out.flush();
                out.close();

                result.put("fileName", outFile.getName());
                System.gc();
            }else {
                result.put("flag", "fail");
                result.put("msg", "没有查询到可下载的数据(No data can be downloaded)");
            }
        } catch (Exception e) {
            logger.error("下载Excel失败", e);
            result.put("flag", "fail");
            result.put("msg", ExceptionUtil.getRootCauseMessage(e));
        }

        return result.getJson();

    }

}
