package welfare.system.controller;

import jakarta.annotation.Resource;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;
import welfare.system.model.ENUM.ClassifyTypeEnum;
import welfare.system.model.po.ArticleClassification;
import welfare.system.model.vo.Response;
import welfare.system.service.ClassificationService;

import java.util.Map;

@RestController
@RequestMapping("/tang-org")
public class ClassificationController {

    @Resource
    ClassificationService classificationService;

    /*
    * 添加版块
    * */
    @PostMapping("/admin/classify/section/add")
    public Response addSection(@RequestBody ArticleClassification section) {
        return classificationService.addSection(section);
    }

    /*
    * 添加项目
    * */
    @PostMapping("/admin/classify/project/add")
    public Response addAchieveProject(@RequestBody ArticleClassification project) {
        return classificationService.addProject(project);
    }

    /*
    * 添加时期
    * */
    @PostMapping("/admin/classify/period/add")
    public Response addPeriod(@RequestBody ArticleClassification period) {
        return classificationService.addPeriod(period);
    }

    /*
    * 更新分类
    * */
    @PostMapping("/admin/classify/update")
    public Response updateClassify(@RequestBody ArticleClassification articleClassification) {
        return classificationService.updateClassification(articleClassification);
    }

    /*
    * 删除板块
    * */
    @PostMapping("/admin/classify/delete")
    public Response deleteClassification(@RequestBody Map<String,Integer> id) {
        return classificationService.deleteClassification(id.get("id"));
    }


    /*
    * 获取某个分类
    * */
    @GetMapping("/admin/classify/view")
    public Response viewClassification(@RequestParam int id) {
        return classificationService.viewClassification(id);
    }


    /*
    * 获取所有版块
    * */
    @GetMapping("/classify/section/all")
    public Response getAllSection() {
        return classificationService.getClassificationByType(-1, ClassifyTypeEnum.SECTION);
    }

    /*
     * 获取某个版块下的项目
     * */
    @GetMapping("/classify/project")
    public Response getProject(@PathParam("upperId") int upperId) {
        return classificationService.getClassificationByType(upperId, ClassifyTypeEnum.PROJECT);
    }

    /*
     * 获取所有时间段
     * */
    @GetMapping("/classify/tag/period")
    public Response getAllPeriod() {
        return classificationService.getClassificationByType(-1,ClassifyTypeEnum.PERIOD);
    }






    /*
    * 获取所有分类及以下文章
    * */
    @GetMapping("/admin/classify/all")
    public Response getAllClassification() {
        return classificationService.getAllClassification();
    }

}
