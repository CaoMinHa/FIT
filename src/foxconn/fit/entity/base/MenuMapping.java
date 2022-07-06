package foxconn.fit.entity.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "FIT_MENU_MAPPING")
public class MenuMapping extends IdEntity implements Serializable {
    private String menuName;
    private String menuLevel;
    private String menuCode;
    private String menuNameE;
    private String menuSort;

    public MenuMapping(){}

    @Override
    public String toString() {
        return "MenuMapping{" +
                "menuName='" + menuName + '\'' +
                ", menuLevel='" + menuLevel + '\'' +
                ", menuCode='" + menuCode + '\'' +
                ", menuNameE='" + menuNameE + '\'' +
                ", menuSort='" + menuSort + '\'' +
                '}';
    }

    public MenuMapping(String menuName, String menuLevel, String menuCode, String menuNameE, String menuSort) {
        this.menuName = menuName;
        this.menuLevel = menuLevel;
        this.menuCode = menuCode;
        this.menuNameE = menuNameE;
        this.menuSort = menuSort;
    }

    /**MENU_NAME*/
    @Column(name="menu_name")
    public String getMenuName() {
        return menuName;
    }
    /**MENU_LEVEL*/
    @Column(name="menu_level")
    public String getMenuLevel() {
        return menuLevel;
    }
    /**MENU_CODE*/
    @Column(name="menu_code")
    public String getMenuCode() {
        return menuCode;
    }
    /**MENU_NAME_N*/
    @Column(name="menu_name_e")
    public String getMenuNameE() {
        return menuNameE;
    }
    /**MENU_SORT*/
    @Column(name="menu_sort")
    public String getMenuSort() {
        return menuSort;
    }
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setMenuLevel(String menuLevel) {
        this.menuLevel = menuLevel;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public void setMenuNameE(String menuNameE) {
        this.menuNameE = menuNameE;
    }

    public void setMenuSort(String menuSort) {
        this.menuSort = menuSort;
    }
}
