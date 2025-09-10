package welfare.system.model.dto.result;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
//封装分页查询的结果
public class PageResultData<T> {
    Integer pageNumber; // 总页码
    Integer totalNum; // 总数据量
    List<T> list;
    List<Map<String, Object>> classificationList; // 某分类列表

    public PageResultData(int pageNumber, int totalNum, List<T> list) {
        this.pageNumber = pageNumber;
        this.totalNum = totalNum;
        this.list = list;
    }

    public Map<String,Object> toReturnMapExceptClassificationList() {
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("pageNumber",pageNumber);
        returnMap.put("totalNum",totalNum);
        returnMap.put("list",list);
        return returnMap;
    }

}
