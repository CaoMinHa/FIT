package foxconn.fit.controller.budget;

import foxconn.fit.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件下载
 */
@Controller
@RequestMapping("/budget/fileDownload")
public class FileDownloadController extends BaseController {

    @RequestMapping(value = "index")
    public String index(Model model, HttpServletRequest request) {
        try {
            String realPath = request.getRealPath("");
            //获取存储路径文件夹
            File outFile=new File(realPath+File.separator+"static"+File.separator+"file");
            List<String> list=new ArrayList<>();
            if(outFile.isDirectory()) {
                File[] files = outFile.listFiles();
                for (int i = 0; i < files.length; i++) {
                    list.add(files[i].getName());
                }
            }
            model.addAttribute("fileNameList",list);
        } catch (Exception e) {
            logger.error("獲取服務器文件夾失敗！（Failed to get server folder）", e);
        }
        return "/budget/fileDownload/index";
    }
}