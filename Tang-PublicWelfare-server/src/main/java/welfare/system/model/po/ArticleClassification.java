package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.ENUM.ClassifyTypeEnum;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@TableName("article_classification")
public class ArticleClassification {
    @TableId(type = IdType.AUTO)
    protected Integer id;

    protected String classifyName;
    protected String classifyIntroduction;
    protected Integer classifyCover = 1;
    protected ClassifyTypeEnum classifyType = ClassifyTypeEnum.OTHER;
    protected Integer articleNum = 0;
    protected Integer classifyRank = 0;
    protected Integer upperClassify = -1;

    public boolean checkUpperClassify() {
        return MAPPER.classify.checkClassifyType(upperClassify,classifyType.getUpper());
    }

    public boolean hasUpperClassify() {
        return upperClassify != null && upperClassify != -1;
    }

    public void setClassifyTypeByQuery() {
        classifyType = MAPPER.classify.getClassifyTypeById(id);
    }

    public Map<String,Object> toReturnMap() {
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("id",id);
        returnMap.put("classifyName",classifyName);
        returnMap.put("classifyRank",classifyRank);
        return returnMap;
    }

    @SuppressWarnings("unused")
    public void setClassifyIntroduction(String classifyIntroduction) {
        if (this.classifyIntroduction == null) {
            this.classifyIntroduction = classifyIntroduction;
        }
    }

    @SuppressWarnings("unused")
    public void setColor(String color) {
        if (classifyIntroduction == null) {
            classifyIntroduction = color;
        }
    }

}
