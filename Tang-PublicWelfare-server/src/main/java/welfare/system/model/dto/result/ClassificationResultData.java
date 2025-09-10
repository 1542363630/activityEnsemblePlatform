package welfare.system.model.dto.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.VALUE;
import welfare.system.model.po.ArticleClassification;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ClassificationResultData extends ArticleClassification {
    private String coverURL;
    private ClassificationResultData upperClassification;

    @Override
    public Map<String,Object> toReturnMap() {
        Map<String,Object> returnMap = super.toReturnMap();
        switch (classifyType) {
            case SECTION -> {
                returnMap.put("classifyIntroduction",classifyIntroduction);
                returnMap.put("coverURL",coverURL);
            }
            case PROJECT -> {
                returnMap.put("classifyIntroduction",classifyIntroduction);
                returnMap.put("coverURL",coverURL);
                returnMap.put("articleNum",articleNum);
                returnMap.put("upperId",upperClassify);
            }
            case PERIOD -> {
                returnMap.put("color",classifyIntroduction);
                returnMap.put("articleNum",articleNum);
            }
        }
        if (upperClassification != null) {
            returnMap.put("upperClassification",upperClassification.toReturnMap());
        }
        return returnMap;
    }

    public Map<String,Object> toSimpleReturnMap() {
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("id",id);
        returnMap.put("classifyName",classifyName);
        returnMap.put("classifyType",classifyType);
        if (upperClassification != null) {
            returnMap.put("upperClassification",upperClassification.toSimpleReturnMap());
        }
        return returnMap;
    }

    @SuppressWarnings("unused")
    public void setCoverURL(String coverName) {
        coverURL = VALUE.web_path + VALUE.img_web + coverName;
    }

}
