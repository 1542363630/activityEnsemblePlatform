package welfare.system.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import welfare.system.model.ENUM.OrganizationEnum;
import welfare.system.model.dto.page.PageData;
import welfare.system.model.dto.result.ArticleSimpleData;
import welfare.system.model.dto.search.UserSearchData;
import welfare.system.model.po.User;
import welfare.system.model.vo.Response;
import welfare.system.service.ArticleService;
import welfare.system.service.CarouselPhotoService;
import welfare.system.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/tang-org/admin")
public class AdminController {

    @Resource
    CarouselPhotoService carouselPhotoService;
    @Resource
    ArticleService articleService;
    @Resource
    UserService userService;


    //管理首页轮播图片接口

    /*
    * 添加轮播图片
    * */
    @PostMapping("/home-page/carousel-photo/add")
    public Response addCarouselPhoto(@RequestBody Map<String,Integer> idAndLast) {
        return carouselPhotoService.addPosterShowOnTop(idAndLast.get("id"),idAndLast.get("last"));
    }

    /*
    * 删除轮播图片
    * */
    @PostMapping("/home-page/carousel-photo/delete")
    public Response deleteCarouselPhoto(@RequestBody Map<String,Integer> id) {
        return carouselPhotoService.deletePosterShowOnTop(id.get("id"));
    }

    //统一管理文章接口

    /*
    * 标记删除文章
    * */
    @PostMapping("/article/delete")
    public Response deleteArticle(@RequestBody ArticleSimpleData article) {
        return articleService.deleteArticle(article.getId(),article.getType());
    }

    /*
     * 更改文章分类和标签
     * */
    @PostMapping("/classify/change")
    public Response changeClassification(@RequestBody ArticleSimpleData articleSimpleData) {
        return articleService.changeArticleClassification(articleSimpleData);
    }

    //管理人员接口

    /*
     * 返回人员列表
     * */
    @PostMapping("/member/all")
    public Response getUserList(HttpServletRequest httpServletRequest, @RequestBody PageData pageData) {
        return userService.getUserList((User) httpServletRequest.getAttribute("user"),pageData,OrganizationEnum.Tang);
    }

    /*
    * 查询人员
    * */
    @PostMapping("/member/search")
    public Response searchUser(HttpServletRequest httpServletRequest, @RequestBody UserSearchData userSearchData) {
        return userService.searchUser((User) httpServletRequest.getAttribute("user"),userSearchData,OrganizationEnum.Tang);
    }

    /*
     * 删除(封禁)人员
     * */
    @PostMapping("/member/delete")
    public Response deleteUser(@RequestBody Map<String,Integer> uid) {
        return userService.deleteUser(uid.get("uid"));
    }

    /*
     * 更改人员身份
     * 由于暂时没有多社团，此处简单化处理
     * 有多社团后，此处需要更改
     * */
    @PostMapping("/member/set")
    public Response setUserAuthority(@RequestBody Map<String,Integer> user) {
        return userService.setUserAuthority(user.get("uid"),user.get("authority"),OrganizationEnum.Tang);
    }

}
