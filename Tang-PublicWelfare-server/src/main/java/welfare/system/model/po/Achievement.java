package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.ENUM.ArticleTypeEnum;
import welfare.system.model.ENUM.ClassifyTypeEnum;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@TableName("achievement")
public class Achievement extends Article {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer articleId;
    private Integer projectId;  // 项目id
    private Integer periodId;  // 时间段id
    private int status = 0;  // 0无 1展示 2删除

    public String check() {
        if (!checkClassification()) return "请检查项目id和时间线id!";
        return super.check();
    }

    public boolean checkClassification() {
        return MAPPER.classify.checkClassifyType(periodId, ClassifyTypeEnum.PERIOD)
                && MAPPER.classify.checkClassifyType(projectId,ClassifyTypeEnum.PROJECT);
    }

    public Map<String, Object> toReturnMap() {
        Map<String,Object> returnMap = super.toReturnMap();
        returnMap.put("id", id);
        returnMap.put("period", periodId == null ? null:MAPPER.classify.getClassifyById(periodId).toReturnMap());
        returnMap.put("project", projectId == null ? null:MAPPER.classify.getClassifyById(projectId).toReturnMap());
        return returnMap;
    }

    public void setArticleType(ArticleTypeEnum articleType) {
        super.setTypeEnum(articleType);
    }

    @SuppressWarnings("unused")
    public void setDisplay(boolean display) {
        status = display ? 1 : 0;
    }
}
