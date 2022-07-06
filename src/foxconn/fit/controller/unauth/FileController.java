package foxconn.fit.controller.unauth;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/unauth/file")
public class FileController {

	@RequestMapping("/download")
	public ResponseEntity<byte[]> download(HttpServletRequest request,String fileName) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		File file = new File(request.getRealPath("")+File.separator+"static"+File.separator+"download" + File.separator + fileName);

		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", fileName);

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
	}

}
