package welfare.system.service;

import jakarta.servlet.http.HttpServletRequest;
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
import welfare.system.model.dto.search.TrainSearchData;
import welfare.system.model.po.TrainResource;
import welfare.system.model.po.User;
import welfare.system.model.vo.CommonErr;
import welfare.system.model.vo.Response;
import welfare.system.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TrainResourceService {

    public Response postTrainResource(HttpServletRequest request, ArticlePostData<TrainResource> articlePostData){
        TrainResource trainResource = articlePostData.getArticle();

        //设置文章发布者uid
        trainResource.setPostUid(((User) request.getAttribute("user")).getUid());
        //设置文章类型
        trainResource.setTypeEnum(ArticleTypeEnum.TRAIN);

        // 将article存入数据库
        int articleId;
        try {
            articleId = MAPPER.article.postArticle(trainResource);
        }catch (RuntimeException e){
            return Response.failure(400, "文章上传失败了QwQ");
        }

        // 将trainResource和引用文件存入数据库
        trainResource.setArticleId(articleId);
        try{
            //trainResource插入数据库
            int id = MAPPER.train.uploadTrainResource(trainResource);
            try {
                //引用关系存入数据库
                FileUtil.recordQuote(articleId, articlePostData.getFileQuote());
            } catch (RuntimeException e) {
                //出错，删除trainResource
                MAPPER.train.removeTrainResourceById(id);
                System.out.println(e.getMessage());
                return Response.failure(400,"请检查文件引用是否正确!");
            }
        } catch (RuntimeException e) {
            MAPPER.article.deleteArticleById(articleId);
            return Response.failure(400,"文章上传失败了QwQ");
        }

        return Response.ok();
    }

    public Response getTrainById(int id) {
        try {
            // 联表查询 trainResource(Article)
            TrainResource trainResource = MAPPER.train.selectById(id);
            if (trainResource != null) {
                return Response.success(trainResource.toReturnMap());
            }
            else {
                return Response.failure(CommonErr.NO_DATA);
            }
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return Response.failure(400, "培训资料查找失败");
        }
    }

    // 通过 板块——项目——时间线 分页查询培训资源
    public Response queryBySectionAndProjectAndPeriod(ArticlePageData articlePageData){
        try {
            articlePageData.setArticleTypeEnum(ArticleTypeEnum.TRAIN);
            // 查询页数不得为负数
            if (articlePageData.getPage() <= 0) return Response.failure(404, "查询页数只能为正");

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
            List<Map<String,String>> trainList = MAPPER.train.getTrainByProjectAndPeriod(articlePageData);
            List<ClassificationResultData> periodList = MAPPER.classify.getClassifyListByType(ClassifyTypeEnum.PERIOD);

            //将查询结果转换为指定的Map
            List<Map<String,Object>> periodResultList = new ArrayList<>();
            for (ClassificationResultData i : periodList) {
                periodResultList.add(i.toReturnMap());
            }

            for(Map<String,String> m : trainList) {
                m.put("coverURL", VALUE.web_path + VALUE.img_web + m.get("coverURL"));
            }

            // 将搜索结果和总页数封装返回
            return Response.success(new PageResultData<>(totalPage, totalNum, trainList, periodResultList));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("查询失败");
        }
    }

    public Response searchTrain(TrainSearchData trainSearchData){
        try {
            PageData pageData = trainSearchData.getPageData();
            String searchSql = trainSearchData.getSearchSql();

            int totalNum = MAPPER.train.getTrainSearchNum(searchSql);
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
                    MAPPER.train.searchTrain(searchSql, pageData.getOffset(), pageData.getNum())
                            .stream()
                            .map(TrainResource::toReturnMap)
                            .toList(),
                    null
            );
            return Response.success(pageResultData.toReturnMapExceptClassificationList());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return Response.failure(400, "查找失败");
        }
    }

}
