package welfare.system.model.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.ENUM.ArticleTypeEnum;
import welfare.system.model.dto.page.PageData;
import welfare.system.util.ArrayUtil;
import welfare.system.util.DateUtil;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("unused")
public class ArticleSearchData {
    private ArticleTypeEnum articleType;
    private PageData pageData;
    private boolean launchTime = false;  // false:不按launchTime排序
    private boolean positiveOrder = false;  // false:DESC  true:ASC
    private Integer postUid = null;  // null:不按发布者搜索
    private Integer projectId = null;  // null:不按项目搜索
    private Integer[] period = null;  // null:不按时间线搜索
    private String searchText = null;  // null或空:不启用搜索 在title introduction content中搜索

    // preTime~~afterTime 表示搜索的时间段 (格式为yyyy-MM-dd)
    private String preTime, afterTime = null;  // null:不按时间段搜索

    public String getSearchSql() {
        StringBuilder sql = new StringBuilder();
        if (articleType == ArticleTypeEnum.ACHIEVEMENT) {
            sql.append(" FROM `achievement` A JOIN `article` B ON A.`article_id` = B.`id` WHERE A.`status` != 2 ");
        }
        // 搜索
        if (postUid != null) {
            sql.append("AND B.`post_uid`=").append(postUid).append(" ");
        }
        if (projectId != null) {
            sql.append("AND A.`project_id`=").append(projectId).append(" ");
        }
        if (period != null && period.length > 0) {
            sql.append("AND A.`period_id` IN ").append(ArrayUtil.arrayToString(period)).append(" ");
        }
        if (preTime != null && afterTime != null) {
            sql.append("AND (B.`launch_time` BETWEEN '")
                    .append(preTime)
                    .append("' AND '")
                    .append(afterTime)
                    .append("') ");
        }
        if (!(searchText == null || searchText.isBlank())) {
            sql.append("AND LOCATE('").append(searchText).append("',B.`title`)>0 ");
        }
        if (launchTime) {
            sql.append("ORDER BY `launch_time` ").append(positiveOrder ? "ASC " : "DESC ");
        }

        return String.valueOf(sql);
    }

    public ArticleSearchData(ArticleTypeEnum articleType) {
        this.articleType = articleType;
    }

    public void setProjectId(Integer projectId) {
        if (projectId != null && projectId != -1){
            this.projectId = projectId;
        }
    }

    public void setPeriod(Integer[] period){
        if (period != null && period.length > 0 && period[0] != -1) {
            this.period = period;
        }
    }

    public void setPreTime(String preTime) {
        if (preTime != null && !preTime.isBlank() && DateUtil.checkStringByFormat(preTime,DateUtil.dayFormat)) this.preTime = preTime;
    }

    public void setAfterTime(String afterTime) {
        if (afterTime != null && !afterTime.isBlank()) this.afterTime = DateUtil.addDaysOnString(afterTime,1);
    }
}
