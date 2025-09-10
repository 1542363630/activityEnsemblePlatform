package welfare.system.model.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.ENUM.Gender;
import welfare.system.model.ENUM.OrganizationEnum;
import welfare.system.model.dto.page.PageData;
import welfare.system.util.CheckUtil;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchData {
    private PageData pageData;
    private int sortOrder = 0;  //0:按注册时间排序  1:按性别排序  2:按sid排序  3:按姓名排序  4:按组织排序
    private boolean positiveOrder = false;  //false:DESC  true:ASC
    private Gender gender = null;  //null:所有性别
    private OrganizationEnum orgName = null;  //null:不按组织搜索
    private Integer authority = null;  //null:不按权限搜索
    private String searchText = null;  //null或空:不启用搜索

    public String getSearchSql(int searchRequestAuthority, OrganizationEnum searchRequestOrg) {
        StringBuilder sql = new StringBuilder();
        //搜索
        boolean first = true;
        if (gender != null) {
            sql.append(" WHERE ");
            first = false;
            sql.append("`gender`='").append(gender).append("' ");
        }

        //如果权限不为三，即是某一个社团的管理员而不是超级管理员，则只能看见自己社员的社团情况
        if (searchRequestAuthority != 3) {
            if (first) {
                sql.append(" WHERE ");
                first = false;
            }
            else sql.append("AND ");
            //如果设置搜索权限 authority映射为普通成员0，社团成员1，社团管理员2，超级管理员3
            if (authority != null) {
                if (authority == 0) {
                    sql.append("`authority`=").append(1).append(" ");
                    sql.append("AND `org_name`=").append(OrganizationEnum.None.id).append(" ");
                } else {
                    sql.append("`authority`=").append(authority).append(" ");
                    sql.append("AND `org_name`=").append(searchRequestOrg.id).append(" ");
                }
            } else {
                sql.append("(`org_name`=").append(searchRequestOrg.id).append(" ");
                sql.append("OR `org_name`=").append(OrganizationEnum.None.id).append(") ");
            }
        }
        //如果权限为三，则可以按组织搜索
        else {
            if (orgName != null){
                if (first) {
                    sql.append(" WHERE ");
                    first = false;
                }
                else sql.append("AND ");
                sql.append("`org_name`=").append(orgName.id).append(" ");
            }

            if (authority != null) {
                if (first) {
                    sql.append(" WHERE ");
                    first = false;
                }
                else sql.append("AND ");
                sql.append("AND `authority`=").append(authority).append(" ");
            }
        }

        if (!(searchText == null || searchText.isBlank())) {
            if (first) {
                sql.append(" WHERE ");
            }
            else sql.append("AND ");
            if (CheckUtil.checkSidSearch(searchText)) {
                sql.append("(LOCATE('").append(searchText).append("',`sid`)>0 OR ")
                    .append("LOCATE('").append(searchText).append("',`real_name`)>0) ");
            }
            else {
                sql.append("LOCATE('").append(searchText).append("',`real_name`)>0 ");
            }
        }
        //排序
        sql.append("ORDER BY ")
           .append(switch (sortOrder) {
            case 1 -> "`gender`,`register_date` ";
            case 2 -> "`sid` ";
            case 3 -> "CONVERT(`real_name` USING gbk) COLLATE gbk_chinese_ci ";
            case 4 -> "`org_name`,`register_date` ";
            default -> "`register_date` ";
        }) .append(positiveOrder ? "ASC " : "DESC ");

        return String.valueOf(sql);
    }

    @SuppressWarnings("unused")
    public void setGender(String gender) {
        if (gender != null) {
            try {
                this.gender = Gender.valueOf(gender);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @SuppressWarnings("unused")
    public void setOrgName(String orgName) {
        if (orgName != null) {
            try {
                this.orgName = OrganizationEnum.valueOf(orgName);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @SuppressWarnings("unused")
    public void setAuthority(Integer authority) {
        if (authority != null && 0 <= authority && authority <= 3) {
            this.authority = authority;
        }
    }
}
