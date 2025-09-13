package welfare.system.service;

import org.springframework.stereotype.Service;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.ENUM.ArticleTypeEnum;
import welfare.system.model.dto.result.ArticleSimpleData;
import welfare.system.model.po.Article;
import welfare.system.model.vo.Response;

@Service
public class ArticleService {

    // 根据id和type更改文章
    public Response deleteArticle(Integer id, ArticleTypeEnum type, Integer postUid) {
        try {
            Article article = MAPPER.article.checkActivityByIdAndPostUid(id, postUid);
            if (article == null) throw new RuntimeException("No authority for this article");
            switch (type) {
                case ACHIEVEMENT -> MAPPER.achieve.deleteAchieveById(id);
                case NEWS -> MAPPER.news.deleteNewsById(id);
                case TRAIN -> MAPPER.train.deleteTrainResourceById(id);
                case ACTIVITY -> MAPPER.activity.deleteActivityById(id);
            }
        }catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return Response.failure(400,"文章删除失败!");
        }
        return Response.ok();
    }


    public Response changeArticleClassification(ArticleSimpleData articleSimpleData) {
        if ((articleSimpleData.getProjectId() != null || articleSimpleData.getPeriodId() != null) && articleSimpleData.checkClassification()) {
            if(switch (articleSimpleData.getType()) {
                case ACHIEVEMENT -> MAPPER.classify.updateAchievement(articleSimpleData);
                case ACTIVITY -> MAPPER.classify.updateActivity(articleSimpleData);
                case TRAIN -> MAPPER.classify.updateTrainResource(articleSimpleData);
                case NEWS -> false;
            }) return Response.ok();
            return Response.failure(400,"无效的文章!");
        }
        return Response.failure(400,"请正确上传更改位置");
    }

}
