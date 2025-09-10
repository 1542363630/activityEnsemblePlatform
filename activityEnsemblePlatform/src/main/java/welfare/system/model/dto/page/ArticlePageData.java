package welfare.system.model.dto.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.ENUM.ArticleTypeEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePageData extends PageData {
    private Integer sectionId = -1, projectId = -1;
    private Integer[] period = null, project = null;  // project 用来放根据 sectionId 查到的 projectId
    ArticleTypeEnum articleTypeEnum;

    public boolean checkPeriod(){
        return period != null && period.length != 0 && !(period.length == 1 && period[0] == -1);
    }

    public boolean checkProjects(){
        return project != null && project.length != 0;
    }

    public boolean checkProject(){
        return projectId != -1;
    }
    public boolean checkSection(){
        return sectionId != -1;
    }

    // 计算并返回总数据量
    public int calculateTotalNum() {
        if (checkProject() || !(checkProject() || checkProjects())) {
            if (articleTypeEnum == ArticleTypeEnum.ACHIEVEMENT) {
                totalNum += MAPPER.achieve.getAchieveNum(projectId, period);
            }
        }
        else {
            if (articleTypeEnum == ArticleTypeEnum.ACHIEVEMENT) {
                totalNum += MAPPER.achieve.getAchieveNumByProjects(project, period);
            }
        }
        return totalNum;
    }

    @SuppressWarnings("unused")
    public void setNum() {
        num = num<1 || num>10 ? 5 : num;
    }
}
