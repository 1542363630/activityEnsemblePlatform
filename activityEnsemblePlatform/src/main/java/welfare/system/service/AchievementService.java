package welfare.system.service;

import org.springframework.stereotype.Service;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.CONSTANT.VALUE;
import welfare.system.model.ENUM.ArticleTypeEnum;
import welfare.system.model.ENUM.ClassifyTypeEnum;
import welfare.system.model.dto.*;
import welfare.system.model.dto.page.ArticlePageData;
import welfare.system.model.dto.page.PageData;
import welfare.system.model.dto.result.ClassificationResultData;
import welfare.system.model.dto.result.PageResultData;
import welfare.system.model.dto.search.AchieveSearchData;
import welfare.system.model.po.Achievement;
import welfare.system.model.vo.CommonErr;
import welfare.system.model.vo.Response;
import welfare.system.util.FileUtil;

import java.util.*;

@Service
public class AchievementService {

    public Response uploadAchievement(int uid, ArticlePostData<Achievement> achievementArticle) {
        Achievement achievement = achievementArticle.getArticle();

        //设置文章发布者uid
        achievement.setPostUid(uid);
        //设置文章类型
        achievement.setArticleType(ArticleTypeEnum.ACHIEVEMENT);

        //文章存入数据库
        int articleId;
        try {
            articleId = MAPPER.article.postArticle(achievement);
        } catch (RuntimeException e) {
            return Response.failure(400, "文章上传失败了QwQ");
        }

        achievement.setArticleId(articleId);
        try {
            int id = MAPPER.achieve.uploadAchievement(achievement);
            try {
                //引用关系存入数据库
                FileUtil.recordQuote(articleId, achievementArticle.getFileQuote());
            } catch (RuntimeException e) {
                //出错，删除achievement
                MAPPER.achieve.removeAchieveById(id);
                System.out.println(e.getMessage());
                return Response.failure(400,"请检查文件引用是否正确!");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            MAPPER.article.deleteArticleById(articleId);
            System.out.println(e.getMessage());
            throw new RuntimeException("文章上传失败了QwQ");
        }

        return Response.ok();
    }

    // 通过 id 查找历史成就
    public Response queryById(int id) {
        try {
            //查询历史成就
            Achievement achievement = MAPPER.achieve.selectById(id);
            if(achievement != null){
                return Response.success(achievement.toReturnMap());
            }
            else {
                return Response.failure(CommonErr.NO_DATA);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("历史成就查找失败了");
        }
    }

    // 通过 板块——项目——时间线 分页查询历史成就
    public Response queryBySectionAndProjectAndPeriod(ArticlePageData articlePageData){
        try {
            articlePageData.setArticleTypeEnum(ArticleTypeEnum.ACHIEVEMENT);
            if (articlePageData.getPage() <= 0) return Response.failure(401, "查询页数只能为正");

            // 没有 project 就查 section，获取 projectId 列表
            if (!articlePageData.checkProject() && articlePageData.checkSection()) {
                List<Integer> projectIdList = MAPPER.classify.getIdListByUpperClassify(articlePageData.getSectionId(), ClassifyTypeEnum.PROJECT);
                articlePageData.setProject(projectIdList.toArray(Integer[]::new));
            }

            // 获取总数据量
            int totalNum = articlePageData.calculateTotalNum();
            int totalPage = articlePageData.calculateTotalPage();
            if (articlePageData.getTotalNum() == 0) {
                return Response.failure(CommonErr.NO_DATA);
            }
            if (articlePageData.getPage() > totalPage) {
                return Response.failure(404, "页数过大!总页数：" + totalPage);
            }

            // 计算跳过的数据条数
            articlePageData.calculateOffset();

            // 获取成就信息(真的麻烦qwq)
            List<Map<String, String>> achieveList;
            if (articlePageData.checkPeriod() && articlePageData.checkProject()) {
                achieveList = MAPPER.achieve.getAchievementsByProjectAndPeriod(articlePageData);
            }
            else if (!articlePageData.checkProject() && articlePageData.checkPeriod()){
                if (articlePageData.checkProjects()) {
                    achieveList = MAPPER.achieve.getAchievementsByProjectsAndPeriod(articlePageData);
                }
                else {
                    achieveList = MAPPER.achieve.getAchievementsByPeriod(articlePageData);
                }
            }
            else if (!articlePageData.checkPeriod() && articlePageData.checkProject()) {
                achieveList = MAPPER.achieve.getAchievementsByProject(articlePageData);
            }
            else if (articlePageData.checkProjects()) {
                achieveList = MAPPER.achieve.getAchievementsByProjects(articlePageData);
            }
            else {
                achieveList = MAPPER.achieve.getAchievements(articlePageData);
            }

            // 获取时间线信息
            List<ClassificationResultData> periodList = MAPPER.classify.getClassifyListByType(ClassifyTypeEnum.PERIOD);
            //将查询结果转换为指定的Map
            List<Map<String,Object>> periodResultList = new ArrayList<>();
            for (ClassificationResultData i : periodList) {
                periodResultList.add(i.toReturnMap());
            }
            // 最后加上 cover 的URL
            for(Map<String,String> m : achieveList) {
                m.put("coverURL", VALUE.web_path + VALUE.img_web + m.get("coverURL"));
            }

            // 将搜索结果和总页数封装返回
            return Response.success(new PageResultData<>(totalPage, totalNum, achieveList, periodResultList));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
            throw new RuntimeException("查询失败");
        }
    }

    public Response searchAchieve(AchieveSearchData achieveSearchData){
        try {
            PageData pageData = achieveSearchData.getPageData();
            String searchSql = achieveSearchData.getSearchSql();

//            System.out.println(searchSql);
            int totalNum = MAPPER.achieve.getAchieveSearchNum(searchSql);
            if (totalNum == 0) {
                return Response.failure(CommonErr.NO_DATA);
            } else {
                pageData.setTotalNum(totalNum);
            }

            int totalPage = pageData.calculateTotalPage();
            if (pageData.getPage() > totalPage) {
                return Response.failure(404, "页数过大!总页数：" + totalPage);
            }

            pageData.calculateOffset();
            PageResultData<Map<String, Object>> pageResultData = new PageResultData<>(
                    totalPage,
                    totalNum,
                    MAPPER.achieve.searchAchieve(searchSql, pageData.getOffset(), pageData.getNum())
                            .stream()
                            .map(Achievement::toReturnMap)
                            .toList(),
                    null
            );
            return Response.success(pageResultData.toReturnMapExceptClassificationList());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return Response.failure(400, "查找失败");
        }
    }

    public Response getTopAchievements(){
        try {
            List<Map<String, Object>> achievementList = MAPPER.achieve.getTopAchievements();
            for (Map<String, Object> achievement : achievementList) {
                achievement.put("coverURL", VALUE.web_path + VALUE.img_web + achievement.get("coverURL"));
            }
            return Response.success(achievementList);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
            return Response.failure(400, "查询失败");
        }
    }

    // 展示活动
    public Response displayAchievement(Integer[] idList) {
        try {
            MAPPER.achieve.displayAchievement(idList);
            return Response.ok();
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
            return Response.failure(400, "展示成就失败");
        }
    }

    // 不展示活动
    public Response nonDisplayAchievement(Integer[] idList) {
        try {
            MAPPER.achieve.nonDisplayAchievement(idList);
            return Response.ok();
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
            return Response.failure(400, "收起成就失败");
        }
    }

}
