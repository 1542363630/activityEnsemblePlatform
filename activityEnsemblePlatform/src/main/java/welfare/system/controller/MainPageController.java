package welfare.system.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import welfare.system.model.vo.Response;
import welfare.system.service.AchievementService;
import welfare.system.service.CarouselPhotoService;
import welfare.system.service.NewsService;

@RestController
@RequestMapping("/home-page")
public class MainPageController {

    @Resource
    CarouselPhotoService carouselPhotoService;
    @Resource
    AchievementService achievementService;
    @Resource
    NewsService newsService;

    /*
    * 获取首页顶部轮播海报图片
    * */
    @GetMapping("/carousel/top/photo")
    public Response getPosterShowOnTop() {
        return carouselPhotoService.getPosterShowOnTop();
    }

    /*
     * 获取顶部轮播历史成就
     * */
    @GetMapping("/carousel/top/achieve")
    public Response getTopAchievement() {
        return achievementService.getTopAchievements();
    }

    /*
    * 获取中间轮播新闻
    * */
    @GetMapping("/carousel/news")
    public Response queryTopNews(@RequestParam Integer num){
        return newsService.queryTopNews(num);
    }

}
