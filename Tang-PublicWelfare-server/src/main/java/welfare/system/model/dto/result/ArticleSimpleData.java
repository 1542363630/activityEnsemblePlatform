package welfare.system.model.dto.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.ENUM.ArticleTypeEnum;
import welfare.system.model.ENUM.ClassifyTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ArticleSimpleData {
    Integer id;
    String title;
    ArticleTypeEnum type;
    Integer projectId;
    Integer periodId;
    String classifyName;  // 标签名(时间段)
    String classifyIntroduction;  // 简介(标签颜色)

    public Map<String,Object> toReturnMap() {
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("id",id);
        returnMap.put("title",title);
        returnMap.put("type",type);
        List<Map<String,Object>> tags = new ArrayList<>();
        returnMap.put("tags",tags);

        if (periodId != null && classifyName != null) {
            Map<String, Object> periodTag = new HashMap<>();
            tags.add(periodTag);

            periodTag.put("periodId", periodId);
            periodTag.put("periodName", classifyName);
            periodTag.put("color", classifyIntroduction);
        }

        return returnMap;
    }

    public boolean checkClassification() {
        return (periodId == null || MAPPER.classify.checkClassifyType(periodId, ClassifyTypeEnum.PERIOD))
                && (projectId == null || MAPPER.classify.checkClassifyType(projectId, ClassifyTypeEnum.PROJECT));
    }

    @SuppressWarnings("unused")
    public<T> void setType(T type) {
        if (type instanceof Integer) {
            this.type = ArticleTypeEnum.getType((Integer) type);
        } else if (type instanceof String) {
            this.type = ArticleTypeEnum.valueOf((String) type);
        }
    }

    @SuppressWarnings("unused")
    public void setProjectId(Integer projectId) {
        if (projectId != null && projectId >= 0) {
            this.projectId = projectId;
        }
    }

    @SuppressWarnings("unused")
    public void setPeriodId(Integer periodId) {
        if (periodId != null && periodId >= 0) {
            this.periodId = periodId;
        }
    }
}
