package welfare.system.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import welfare.system.model.dto.ArticlePostData;
import welfare.system.model.dto.search.ActivitySearchData;
import welfare.system.model.po.ActivityInfo;
import welfare.system.model.po.User;
import welfare.system.model.vo.Response;
import welfare.system.service.ActivityService;

import java.util.Map;

@RestController
public class ActivityController {
    @Resource
    ActivityService activityService;

    //发布活动
    @PostMapping("/admin/post/activity")
    public Response postActivity(HttpServletRequest request, @RequestBody ArticlePostData<ActivityInfo> activityInfo){
        return activityService.postActivity(request,activityInfo);
    }


    //获取个人活动列表(分页)
    @PostMapping("/user/activity")
    public Response selectActivityByUserId(HttpServletRequest request, @RequestBody Map<String,Integer> pageData){
        return activityService.selectActivityByUserId(((User) request.getAttribute("user")).getUid(),pageData.get("page"),pageData.get("num"));
    }

    //获取活动预览内容
    @GetMapping("/activity/preview")
    public Response previewActivity(@RequestParam Integer num) {
        return activityService.previewActivity(num);
    }

    // //按版块获取活动列表(分项目)
    // @PostMapping("/activity/view")
    // public Response selectActivityBySection(@RequestBody Map<String,Integer> sectionId) {
    //     return activityService.selectActivityBySection(sectionId.get("sectionId"));
    // }

    //活动筛选（按 关键词，是否进行中，项目类型 进行筛选）
    @PostMapping("/activity/constrained")
    public Response selectActivityConstrained(@RequestBody ActivitySearchData activitySearchData){
        return activityService.selectActivityConstrained(activitySearchData);
    }

//    //获取报名未结束活动列表（分页）
//    @PostMapping("/tang-org/activity/ongoing")
//    public Response selectActivityOngoing(@RequestBody Map<String,Integer> pageData){
//        return activityService.selectActivityOngoing(pageData.get("page"));
//    }
//
//    //获取报名已结束活动列表（分页）
//    @PostMapping("/tang-org/activity/ended")
//    public Response selectActivityEnd(@RequestBody Map<String,Integer> pageData){
//        return activityService.selectActivityEnd(pageData.get("page"));
//    }

    //根据id查看活动
    @GetMapping("/activity")
    public Response selectActivityById(HttpServletRequest request,@RequestParam int activityId){
        return activityService.selectActivityById(((User) request.getAttribute("user")).getUid(),activityId);
    }

    //申请参加活动
    @PostMapping("/activity/apply")
    public Response registerActivity(HttpServletRequest request,@RequestBody Map<String,Integer> id){
        return activityService.registerActivity(((User) request.getAttribute("user")).getUid(), id.get("activityId"));
    }

        //申请取消注册活动
        @PostMapping("/activity/cancel")
        public Response cancelActivity(HttpServletRequest request,@RequestBody Map<String,Integer> id){
            return activityService.cancelActivity(((User) request.getAttribute("user")).getUid(), id.get("activityId"));
        }

}
