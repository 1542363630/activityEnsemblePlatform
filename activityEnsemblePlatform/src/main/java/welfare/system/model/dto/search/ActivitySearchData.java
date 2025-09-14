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
public class ActivitySearchData {
    private PageData pageData;
    private int sortOrder = 0;  //0:按报名开始时间排序  1:按报名截止时间排序  2:按活动id排序
    private boolean positiveOrder = false;  //false:DESC  true:ASC
    private String keyword = null;//null or "": 不按关键字搜索
    private Boolean ongoing = null;//null: 不按进行状态搜索
    // private Integer projectId = null;//null or -1: 不按类别搜索

    public String getSearchSql() {
        StringBuilder sql = new StringBuilder();
        //搜索
        boolean first = true;
        if (keyword != null) {
            sql.append(" WHERE ");
            first = false;
            sql.append("(`article`.`title` like ").append("'%").append(keyword).append("%') ");
        }

        if(ongoing != null){
            if(first){
                sql.append(" WHERE ");
                first = false;
            }
            else {
                sql.append("AND ");
            }
            if(ongoing){
                sql.append("(current_date < `activity`.`register_end_time`) ");
            }
            else {
                sql.append("(current_date >= `activity`.`register_end_time`) ");
            }
        }

        // if(projectId != null && projectId != -1) {
        //     if(first){
        //         sql.append(" WHERE ");
        //         first = false;
        //     }
        //     else {
        //         sql.append("AND ");
        //     }

        //     sql.append("`activity`.`project_id` = ").append(projectId).append(" ");
        // }

        //排序
        sql.append("ORDER BY ")
                .append(switch (sortOrder) {
                    case 1 -> "`activity`.`register_end_time` ";
                    case 2 -> "`activity`.`id` ";
                    default -> "`activity`.`register_start_time` ";
                }) .append(positiveOrder ? "ASC " : "DESC ");

        return String.valueOf(sql);
    }
}
