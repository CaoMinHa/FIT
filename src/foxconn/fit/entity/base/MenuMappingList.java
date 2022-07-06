package foxconn.fit.entity.base;

import java.io.Serializable;
import java.util.List;

public class MenuMappingList implements Serializable {
    private String menuName;
    private String menuCode;
    private String menuNameE;
    private List<MenuMapping> list;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuNameE() {
        return menuNameE;
    }

    public void setMenuNameE(String menuNameE) {
        this.menuNameE = menuNameE;
    }

    public List<MenuMapping> getList() {
        return list;
    }

    public void setList(List<MenuMapping> list) {
        this.list = list;
    }
}
