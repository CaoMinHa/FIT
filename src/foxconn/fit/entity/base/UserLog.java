package foxconn.fit.entity.base;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户操作日志
 * 
 * @author liangchen
 *
 */
@Entity
@Table(name = "FIT_USER_LOG")
public class UserLog extends IdEntity {

	private static final long serialVersionUID = -8797605478151216617L;

	private String method;// 方法
	private String parameter;// 参数
	private String status;// 状态
	private String message;// 消息
	private String operator;// 操作人
	private Date operatorTime;// 操作时间

	@Column
	public String getMethod() {
		return method;
	}

	@Column
	public String getParameter() {
		return parameter;
	}

	@Column
	public String getStatus() {
		return status;
	}

	@Column
	public String getMessage() {
		return message;
	}

	@Column
	public String getOperator() {
		return operator;
	}

	@Column(name = "operator_Time")
	public Date getOperatorTime() {
		return operatorTime;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public void setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
	}

}
