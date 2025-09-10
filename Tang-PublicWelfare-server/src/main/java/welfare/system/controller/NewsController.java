package welfare.system.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import welfare.system.model.dto.ArticlePostData;
import welfare.system.model.po.News;
import welfare.system.model.po.User;
import welfare.system.model.vo.Response;
import welfare.system.service.NewsService;

import java.util.Map;

@RestController

public class NewsController {
    @Resource
    NewsService newsService;

    @PostMapping("/tang-org/admin/post/news")
    public Response postNews(HttpServletRequest request, @RequestBody ArticlePostData<News> news){
        return newsService.postNews(((User) request.getAttribute("user")).getUid(),news);
    }

    @PostMapping("/news/all")
    public Response queryAllNews(@RequestBody Map<String,Integer> pageData){
        return newsService.queryAllNews(pageData.get("page"),pageData.get("num"));
    }

    @PostMapping("/news/keyword")
    public Response selectNewsByKeyword(@RequestBody Map<String,Object> constrained){
        return newsService.selectNewsByKeyword((String) constrained.get("keyword"), (Integer) constrained.get("page"),(Integer) constrained.get("num"));
    }

    @GetMapping("/news")
    public Response queryNews(@RequestParam Integer id) {
        return newsService.queryNewsById(id);
    }

    @GetMapping("/news/recommend")
    public Response recommendNews(@RequestParam String lunchTime,@RequestParam int num) {
        return newsService.recommendNews(lunchTime,num);
    }

}
