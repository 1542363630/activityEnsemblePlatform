package welfare.system.model.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import welfare.system.model.ENUM.ArticleTypeEnum;

@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unused")
public class TrainSearchData extends ArticleSearchData {
    private Integer status = null;

    public TrainSearchData(){
        super(ArticleTypeEnum.TRAIN);
    }

}
