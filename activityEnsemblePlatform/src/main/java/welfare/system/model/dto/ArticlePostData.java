package welfare.system.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.core.exception.CheckException;
import welfare.system.model.po.Article;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePostData<T extends Article> {
    T article;
    Integer[] fileQuote;

    public void setArticle(T article) {
        String checkResult = article.check();
        if (checkResult != null) {
            throw new CheckException(checkResult);
        }
        //转译html为text
        article.setContentAsText();
        this.article = article;
    }

}
