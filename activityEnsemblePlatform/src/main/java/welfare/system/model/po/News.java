package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.ENUM.ArticleTypeEnum;

@Getter
@Setter
@NoArgsConstructor
@TableName("news")
public class News extends Article {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer articleId;
    private int status = 0;  //0发布 1展示 2删除

    public void setArticleType(ArticleTypeEnum articleType) {
        super.setTypeEnum(articleType);
    }

    @SuppressWarnings("unused")
    public void setDisplay(boolean display) {
        status = display ? 1 : 0;
    }
}
