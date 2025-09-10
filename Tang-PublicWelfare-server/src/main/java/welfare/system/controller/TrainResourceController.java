package welfare.system.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import welfare.system.model.dto.ArticlePostData;
import welfare.system.model.dto.page.ArticlePageData;
import welfare.system.model.dto.search.TrainSearchData;
import welfare.system.model.po.TrainResource;
import welfare.system.model.vo.Response;
import welfare.system.service.TrainResourceService;

@RestController
@RequestMapping("/tang-org")
public class TrainResourceController {
    @Resource
    TrainResourceService trainResourceService;

    /*
     * 发布培训资料
     * */
    @PostMapping("/admin/post/train-resource")
    public Response postTrainResource(HttpServletRequest request,@RequestBody ArticlePostData<TrainResource> articlePostData){
        return trainResourceService.postTrainResource(request, articlePostData);
    }

    /*
     * 根据id查找未删培训资料
     * */
    @GetMapping("/train-resource")
    public Response getTrainById(@RequestParam int id){
        return trainResourceService.getTrainById(id);
    }

    /*
     * 根据 “项目——时间线” 分页获取未删除培训资源
     * */
    @PostMapping("/train-resource/project-period")
    public Response queryBySectionAndProjectAndPeriod(@RequestBody ArticlePageData articlePageData){
        return trainResourceService.queryBySectionAndProjectAndPeriod(articlePageData);
    }

    /*
     * 查询资料
     * */
    @PostMapping("/train-resource/search")
    public Response searchAchieve(@RequestBody TrainSearchData trainSearchData) {
        return trainResourceService.searchTrain(trainSearchData);
    }

}
