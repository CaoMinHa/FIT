package foxconn.fit.service.base;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailImpl extends User implements UserDetails{

	private static final long serialVersionUID = 1L;
	
	private String corporationCode;//	法人编码
	private String entity;//	HFM公司编码
	private String ebs;//	EBS公司编码
	private String menus;//	菜单
	private String poCenter;//採購中心
	private String email;
	private String realname;
	private String sbu;

	public UserDetailImpl(String username, String password,String corporationCode,String entity,String ebs,String menus,String poCenter,String email,String realname,String sbu,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.corporationCode=corporationCode;
		this.entity=entity;
		this.ebs=ebs;
		this.menus=menus;
		this.poCenter = poCenter;
		this.email = email;
		this.realname = realname;
		this.sbu = sbu;
	}

	public String getCorporationCode() {
		return corporationCode;
	}

	public String getMenus() {
		return menus;
	}
	
	public String getEntity(){
		return entity;
	}

	public String getEBS() {
		return ebs;
	}

	public String getPoCenter(){
		return poCenter;
	}

	public String getEmail(){
		return email;
	}
	public String getRealname(){
		return realname;
	}
	public String getSbu(){
		return sbu;
	}

}
