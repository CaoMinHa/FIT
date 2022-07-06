package foxconn.fit.service.base;

import foxconn.fit.dao.base.UserDao;
import foxconn.fit.entity.base.MenuMapping;
import foxconn.fit.entity.base.MenuMappingList;
import foxconn.fit.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maggao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuService{
	@Autowired
	private UserDao userDAO;

	public List<MenuMappingList> selectMenu() {
		List<MenuMappingList> menuMappingList=new ArrayList<>();
		String sql = "select * from FIT_MENU_MAPPING where MENU_LEVEL is null order by MENU_SORT";
		List<MenuMapping> list = userDAO.listBySql(sql,MenuMapping.class);

		String [] str=SecurityUtils.getLoginUser().getMenus().split(",");
		String menuStr="";
		for (String s:str) {
			menuStr+="'"+s+"',";
		}

		for (int i=0;i<list.size();i++){
			MenuMapping m=list.get(i);
			sql="select * from FIT_MENU_MAPPING where MENU_LEVEL='"+m.getMenuCode()+"' and MENU_CODE in("+menuStr.substring(0,menuStr.length()-1)+") order by cast(MENU_SORT as int)";
			List<MenuMapping> list1 = userDAO.listBySql(sql,MenuMapping.class);
			MenuMappingList menu=new MenuMappingList();
			menu.setMenuCode(m.getMenuCode());
			menu.setMenuName(m.getMenuName());
			menu.setMenuNameE(m.getMenuNameE());
			menu.setList(list1);
			menuMappingList.add(menu);
		}
		return menuMappingList;
	}
	public List<MenuMapping> selectMenuAll() {
		String sql = "select * from FIT_MENU_MAPPING where MENU_LEVEL is not null order by MENU_LEVEL desc,MENU_SORT";
		List<MenuMapping> list = userDAO.listBySql(sql,MenuMapping.class);
		return list;
	}
}
