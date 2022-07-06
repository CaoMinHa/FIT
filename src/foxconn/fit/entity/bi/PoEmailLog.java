package foxconn.fit.entity.bi;

import org.hibernate.cfg.ImprovedNamingStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "CUX_PO_EMAIL")
public class PoEmailLog extends ImprovedNamingStrategy implements Serializable {

	private static final long serialVersionUID = -2275276207234120757L;
	@Column(name = "created_by")//CREATED_BY
	private String createBy;

	@Column(name = "created_name")//CREATED_NAME
	private String createName;

	@Column(name = "creation_date") //CREATION_DATE
	private Date createDate;
	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "email_title") //EMAIL_TITLE
	private String emailTitle;
	@Column(name = "email_content") //EMAIL_CONTENT
	private String emailContent;
	@Column(name = "email_team") //EMAIL_TEAM
	private String emailTeam;
	@Column(name = "file_name") //FILE_NAME
	private String fileName;
	@Column(name = "file_address") //FILE_ADDRESS
	private String fileAddress;
	@Column(name = "end_date") //END_DATE
	private String endDate;

	public PoEmailLog() {}

	@Override
	public String toString() {
		return "PoEmailLog{" +
				"createBy='" + createBy + '\'' +
				"createName='" + createName + '\'' +
				", createDate=" + createDate +
				", id='" + id + '\'' +
				", emailTitle='" + emailTitle + '\'' +
				", emailContent='" + emailContent + '\'' +
				", emailTeam='" + emailTeam + '\'' +
				", fileName='" + fileName + '\'' +
				", fileAddress='" + fileAddress + '\'' +
				", endDate='" + endDate + '\'' +
				'}';
	}

	public PoEmailLog(String createBy,String createName, Date createDate, String id, String emailTitle, String emailContent, String emailTeam, String fileName, String fileAddress,String endDate) {
		this.createBy = createBy;
		this.createName = createName;
		this.createDate = createDate;
		this.id = id;
		this.emailTitle = emailTitle;
		this.emailContent = emailContent;
		this.emailTeam = emailTeam;
		this.fileName = fileName;
		this.fileAddress = fileAddress;
		this.endDate = endDate;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	public void setEmailTeam(String emailTeam) {
		this.emailTeam = emailTeam;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileAddress(String fileAddress) {
		this.fileAddress = fileAddress;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getCreateBy() {
		return createBy;
	}
	public String getCreateName() {
		return createName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getId() {
		return id;
	}

	public String getEmailTitle() {
		return emailTitle;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public String getEmailTeam() {
		return emailTeam;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileAddress() {
		return fileAddress;
	}

	public String getEndDate() {
		return endDate;
	}
}
