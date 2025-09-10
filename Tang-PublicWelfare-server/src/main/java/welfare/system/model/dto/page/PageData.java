package welfare.system.model.dto.page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageData {
    protected Integer num = 5, page = 1, offset, totalNum = 0;

    //计算跳过条数
    public void calculateOffset() {
        offset = (page - 1) * num;
    }

    // 计算计算并返回总页数
    public int calculateTotalPage() {
        return totalNum%num == 0 ? totalNum/num : totalNum/num + 1;
    }

    @SuppressWarnings("unused")
    public void setNum() {
        num = num<1 || num>20 ? 14 : num;
    }

}
