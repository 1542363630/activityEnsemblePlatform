package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.ENUM.ClassifyTypeEnum;
import welfare.system.model.dto.result.ClassificationResultData;
import welfare.system.service.ClassificationService;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@TableName("activity")
public class Activity extends Article {
    @TableId(type = IdType.AUTO)
    private Integer id;

    protected Integer articleId;
    protected Integer projectId;

    protected Date registerStartTime;
    protected Date registerEndTime;
    private int status = 0;  //0发布 1删除

    @Override
    public String check() {
        if (registerStartTime == null) registerStartTime = new Date();
        if (registerEndTime == null) return "请设定报名截止时间!";
        if (projectId == null) return "需要指定活动所在项目!";
        if (!checkClassification()) return "请检查项目!";
        return super.check();
    }

    public boolean checkClassification() {
        return MAPPER.classify.checkClassifyType(projectId, ClassifyTypeEnum.PROJECT);
    }

    @Override
    public Map<String,Object> toReturnMap() {
        Map<String,Object> returnMap = super.toReturnMap();
        returnMap.put("id",id);
        ClassificationResultData classification = MAPPER.classify.getClassifyById(projectId);
        ClassificationService.setUpperClassification(classification);
        returnMap.put("classification",classification.toSimpleReturnMap());
        returnMap.put("registerStartTime",registerStartTime);
        returnMap.put("registerEndTime",registerEndTime);
        return returnMap;
    }
}
