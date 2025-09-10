package welfare.system.model.dto.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.ENUM.ClassifyTypeEnum;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ClassificationSimpleData {
    private Integer id;
    private String classifyName;
    private ClassifyTypeEnum classifyType;
    private Integer articleNum;
    private Integer classifyRank;
    private Integer upperClassify;

    public Map<String,Object> toReturnMap() {
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("id",id);
        returnMap.put("articleNum",articleNum);
        returnMap.put("classifyName",classifyName);
        returnMap.put("classifyRank",classifyRank);
        return returnMap;
    }
}
