package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.ENUM.ClassifyTypeEnum;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@TableName("train_resource")
public class TrainResource extends Article {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer projectId;  //版块id
    private Integer periodId;  //时间段id
    private Integer articleId;
    private int status = 0;  //0发布 1删除

    @Override
    public String check() {
        if (periodId == null || projectId == null) return "请填写项目和时间线!";
        if (!checkClassification()) return "请检查项目和时间线!";
        return super.check();
    }

    public boolean checkClassification() {
        return MAPPER.classify.checkClassifyType(periodId,ClassifyTypeEnum.PERIOD)
                && MAPPER.classify.checkClassifyType(projectId,ClassifyTypeEnum.PROJECT);
    }

    @Override
    public Map<String, Object> toReturnMap() {
        Map<String, Object> returnMap = super.toReturnMap();
        returnMap.put("id", id);
        returnMap.put("period", periodId == null ? null:MAPPER.classify.getClassifyById(periodId).toReturnMap());
        returnMap.put("project", projectId == null ? null:MAPPER.classify.getClassifyById(projectId).toReturnMap());

        return returnMap;
    }

}
