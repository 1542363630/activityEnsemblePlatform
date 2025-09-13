package welfare.system.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import welfare.system.model.dto.search.AchieveSearchData;
import welfare.system.model.dto.ArticlePostData;
import welfare.system.model.dto.page.ArticlePageData;
import welfare.system.model.po.Achievement;
import welfare.system.model.po.User;
import welfare.system.model.vo.Response;
import welfare.system.service.AchievementService;

import java.util.Map;

@RestController
@RequestMapping("/tang-org")
public class AchievementController {
    @Resource
    AchievementService achievementService;

    /*
     * 发布历史成就
     * */
    @PostMapping("/admin/post/achieve")
    public Response uploadAchievement(HttpServletRequest request, @RequestBody ArticlePostData<Achievement> achievement) {
        return achievementService.uploadAchievement(((User) request.getAttribute("user")).getUid(),achievement);
    }

    /*
     * 根据id获取未删除历史成就
     * */
    @GetMapping("/achieve")
    public Response queryById(@RequestParam int id){
        return achievementService.queryById(id);
    }

    /*
     * 根据 “项目——时间线” 分页获取未删除历史成就
     * */
    // @PostMapping("/achieve/project-period")
    // public Response queryBySectionAndProjectAndPeriod(@RequestBody ArticlePageData articlePageData){
    //     return achievementService.queryBySectionAndProjectAndPeriod(articlePageData);
    // }

    /*
     * 查询成就
     * */
    @PostMapping("/achieve/search")
    public Response searchAchieve(@RequestBody Map<String,Integer> pageData) {
        return achievementService.searchAchieve(pageData.get("page"),pageData.get("num"));
    }

    // 更改 status = 1
    @PostMapping("/admin/achieve/display")
    public Response displayActivity(@RequestBody Map<String,Integer[]> idList){
        return achievementService.displayAchievement(idList.get("idList"));
    }

    // 更改 status = 0
    @PostMapping("/admin/achieve/non-display")
    public Response nonDisplayActivity(@RequestBody Map<String,Integer[]> idList){
        return achievementService.nonDisplayAchievement(idList.get("idList"));
    }

}
