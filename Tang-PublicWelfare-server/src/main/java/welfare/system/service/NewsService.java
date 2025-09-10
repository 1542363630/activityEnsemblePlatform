package welfare.system.service;

import org.springframework.stereotype.Service;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.CONSTANT.VALUE;
import welfare.system.model.ENUM.ArticleTypeEnum;
import welfare.system.model.dto.ArticlePostData;
import welfare.system.model.dto.result.PageResultData;
import welfare.system.model.po.News;
import welfare.system.model.vo.Response;
import welfare.system.util.DateUtil;
import welfare.system.util.FileUtil;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class NewsService {

    //发布新闻
    public Response postNews(int uid, ArticlePostData<News> newsArticle){
        News news = newsArticle.getArticle();

        //设置文章发布者uid
        news.setPostUid(uid);
        //设置文章类型
        news.setArticleType(ArticleTypeEnum.NEWS);

        //文章存入数据库
        int articleId;
        try {
            articleId = MAPPER.article.postArticle(news);
        } catch (RuntimeException e) {
            return Response.failure(400, "文章上传失败了QwQ");
        }

        news.setArticleId(articleId);
        try {
            //news插入数据库
            int id = MAPPER.news.uploadNews(news);
            try {
                //引用关系存入数据库
                FileUtil.recordQuote(articleId, newsArticle.getFileQuote());
            } catch (RuntimeException e) {
                //出错，删除news
                MAPPER.news.removeNewsById(id);
                System.out.println(e.getMessage());
                return Response.failure(400,"请检查文件引用是否正确!");
            }
        } catch (RuntimeException e) {
            //出错，删除article
            MAPPER.article.deleteArticleById(articleId);
            System.out.println(e.getMessage());
            return Response.failure(400,"文章上传失败了QwQ");
        }

        return Response.ok();
    }
    
    //获取所有未删除新闻
    public Response queryAllNews(int page,int pageSize){
        try {
            if(page<=0) return Response.failure(404,"页数只能为正");
            //得到总页数
            int totalNum = MAPPER.news.numberOfAllNews();
            int maxPage = (int) Math.ceil((double) totalNum / pageSize);
            if(page>maxPage){
                return Response.failure(404,"页数超过最大页数。最大页数：" + maxPage);
            }
            //得到指定页
            List<Map<String,Object>> newsList = MAPPER.news.selectNewsByPage(pageSize,(page -1)* pageSize);
            for (Map<String,Object> m : newsList) {
                m.put("coverURL",VALUE.web_path + VALUE.img_web + m.get("coverURL"));
            }

            return Response.success(new PageResultData<>(maxPage,totalNum,newsList).toReturnMapExceptClassificationList());
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return Response.failure(400,"查询失败");
        }
    }

    //获取新闻（按关键词）
    public Response selectNewsByKeyword(String keyword,int page,int pageSize){
        try {
            if(page<=0) return Response.failure(404,"页数只能为正");
            //得到总页数
            int totalNum = MAPPER.news.selectNumberByKeyword(keyword);
            int maxPage = (int) Math.ceil((double) totalNum / pageSize);
            if(page>maxPage){
                return Response.failure(404,"页数超过最大页数。最大页数：" + maxPage);
            }
            List<Map<String,Object>> newsList = MAPPER.news.selectNewsByKeyword(keyword, pageSize,(page -1)* pageSize);
            for (Map<String,Object> m : newsList) {
                m.put("coverURL",VALUE.web_path + VALUE.img_web + m.get("coverURL"));
            }

            return Response.success(new PageResultData<>(maxPage,totalNum,newsList).toReturnMapExceptClassificationList());
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return Response.failure(400,"查询失败");
        }
    }

    //获取轮播新闻
    public Response queryTopNews(Integer num){
        if (num == null || num < 0 || num > 20) num = 5;
        List<Map<String,Object>> newsList = MAPPER.news.selectTopNews(num);
        for (Map<String,Object> m : newsList) {
            m.put("coverURL",VALUE.web_path + VALUE.img_web + m.get("coverURL"));
        }
        return Response.success(newsList);
    }

    //根据id获取新闻
    public Response queryNewsById(int id) {
        News news = MAPPER.news.getNewsById(id);
        return Response.success(news.toReturnMap());
    }

    //在附近的时间内随机获取新闻
    public Response recommendNews(String lunchTime,int num) {
        if (lunchTime != null && !lunchTime.isBlank()) {
            DateUtil.checkStringByFormat(lunchTime,DateUtil.dayFormat);
        }
        if (num < 1 || num > 10) num = 5;
        List<Map<String,Object>> newsList = MAPPER.news.selectRecommendNews(
                DateUtil.addDaysOnString(lunchTime,new Random().nextInt(-10,10)),
                num
        );
        for (Map<String,Object> m : newsList) {
            m.put("coverURL",VALUE.web_path + VALUE.img_web + m.get("coverURL"));
        }
        return Response.success(newsList);
    }

}
