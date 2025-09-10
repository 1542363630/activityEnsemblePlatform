package welfare.system.model.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import welfare.system.model.ENUM.ArticleTypeEnum;

@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unused")
public class AchieveSearchData extends ArticleSearchData {
    private Integer status = null;

    public AchieveSearchData(){
        super(ArticleTypeEnum.ACHIEVEMENT);
    }

}
