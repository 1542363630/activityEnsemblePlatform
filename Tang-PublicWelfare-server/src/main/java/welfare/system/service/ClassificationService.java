package welfare.system.service;

import org.springframework.stereotype.Service;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.ENUM.ClassifyTypeEnum;
import welfare.system.model.dto.result.ArticleSimpleData;
import welfare.system.model.dto.result.ClassificationResultData;
import welfare.system.model.dto.result.ClassificationSimpleData;
import welfare.system.model.po.ArticleClassification;
import welfare.system.model.vo.CommonErr;
import welfare.system.model.vo.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassificationService {

    /*
    * 管理分类
    * 分为添加版块、添加项目、添加时间线、添加其它标签（未加入）、删除分类和标签
    * 一旦决定使标签多元化，应当适当改变结构
    * 数据库中原先的分类的枚举类可能不再适用
    * 当前标签：时间线，是一个特殊化的标签，并非真正的多元化标签
    * 而是关于时期这一类标签的统称，因此可以为其设置一个专门的类型：PERIOD
    * 其它标签则不会出现一个统称，或许可以用OTHER枚举类，但还是要注意需求增加导致不够用的情况
    * 好在分类本身不会产生大量数据，无需担心未来的改动会伤及数据库
    * */

    public Response addSection(ArticleClassification section) {
        section.setUpperClassify(-1);
        section.setClassifyType(ClassifyTypeEnum.SECTION);
        return Response.success(MAPPER.classify.addClassification(section));
    }

    public Response addProject(ArticleClassification project) {
        project.setClassifyType(ClassifyTypeEnum.PROJECT);
        if (project.hasUpperClassify() && project.checkUpperClassify()) {
            return Response.success(MAPPER.classify.addClassification(project));
        } else {
            return Response.failure(400,"上级分类错误!");
        }
    }

    public Response addPeriod(ArticleClassification period) {
        period.setUpperClassify(-1);
        period.setClassifyCover(null);
        period.setClassifyType(ClassifyTypeEnum.PERIOD);
        return Response.success(MAPPER.classify.addClassification(period));
    }

    //更新分类
    public Response updateClassification(ArticleClassification classification) {
        classification.setClassifyTypeByQuery();

        //根据不同分类类型做出不同处理
        switch (classification.getClassifyType()) {
            //如果更新板块
            case SECTION -> classification.setUpperClassify(null);
            //如果更新项目
            case PROJECT -> {
                if (classification.hasUpperClassify() && !classification.checkUpperClassify()) {
                    return Response.failure(400,"该项目要改成的所属板块错误!");
                }
            }
            //如果更新时间线
            case PERIOD -> {
                classification.setUpperClassify(null);
                classification.setClassifyCover(null);
            }
            //如果更新未定义分类
            default -> {
                return Response.failure(400,"该分类类型暂不使用!");
            }
        }

        //更新分类
        MAPPER.classify.updateClassify(classification);

        return Response.ok();
    }

    //删除分类
    public Response deleteClassification(int id) {
        if (id < 5) {
            return Response.failure(400,"该分类不可删除!");
        }

        ClassifyTypeEnum classifyType = MAPPER.classify.getClassifyTypeById(id);
        if (classifyType == null ) {
            return Response.failure(400,"不存在该分类!");
        } else {
            try {
                switch (classifyType) {
                    //如果删除版块
                    case SECTION -> {
                        List<Integer> projectIdList = MAPPER.classify.getIdListByUpperClassify(id, ClassifyTypeEnum.PROJECT);
                        MAPPER.classify.updateAllCausedByProjectChange(projectIdList);
                        projectIdList.add(id);
                        MAPPER.classify.deleteAllClassify(projectIdList);
                    }
                    //如果删除项目或时间段
                    case PROJECT -> {
                        MAPPER.classify.updateOneCausedByProjectChange(id);
                        MAPPER.classify.deleteOneClassify(id);
                    }
                    case PERIOD -> {
                        MAPPER.classify.updateOneCausedByPeriodChange(id);
                        MAPPER.classify.deleteOneClassify(id);
                    }
                    //如果正在删除未定义分类
                    default -> {
                        return Response.failure(400, "无效分类!");
                    }
                }
                return Response.ok();
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                return Response.failure(400,"未知错误，请联系后台!");
            }
        }
    }


    /*
    * 查询分类
    * */

    public Response viewClassification(int id) {
        ClassificationResultData classification = MAPPER.classify.getClassifyById(id);
        if (classification == null) return Response.failure(CommonErr.NO_DATA);

        //递归查询上层分类(目前只有二层)
        setUpperClassification(classification);

        return Response.success(classification);
    }

    public Response getClassificationByType(int upperId, ClassifyTypeEnum classifyType) {
        //查询指定的列表
        List<ClassificationResultData> classificationList;
        if (upperId == -1) {
            classificationList = MAPPER.classify.getClassifyListByType(classifyType);
        } else {
            classificationList = MAPPER.classify.getSectionListByTypeAndUpperClassify(upperId,classifyType);
        }
        //未找到查询结果
        if (classificationList.isEmpty()) {
            return Response.failure(CommonErr.NO_DATA);
        }
        //将查询结果转换为指定的Map
        List<Map<String,Object>> resultList = new ArrayList<>();
        for (ClassificationResultData i : classificationList) {
            resultList.add(i.toReturnMap());
        }
        //如果有上层分类，则向上查询
        if (upperId == -1) return Response.success(resultList);
        else {
            Map<String,Object> resultMap = new HashMap<>();
            ClassificationResultData upperClassification = MAPPER.classify.getClassifyById(upperId);
            resultMap.put("classificationList",resultList);
            resultMap.put("upperClassification",upperClassification.toReturnMap());

            //递归查询上层分类(目前只有二层)
            setUpperClassification(upperClassification);

            return Response.success(resultMap);
        }
    }

    //递归查询上层分类(目前只有二层)
    public static void setUpperClassification(ClassificationResultData classification) {
        while (classification.hasUpperClassify()) {
            Integer upperId = classification.getUpperClassify();
            ClassificationResultData upperUpperClassification = MAPPER.classify.getClassifyById(upperId);
            classification.setUpperClassification(upperUpperClassification);
            classification = upperUpperClassification;
        }
    }

    /*
    * 一次性查看全部分类以及下方文章
    * 目前前端要求一次性返回所有文章
    * 虽然会导致数据量巨大，但在问题不够凸显的情况下暂且这么做
    * 以后出现查询时间过长等问题时修改
    * 因为这样的需求都是临时的所以内部也采用临时方法
    * */
    public Response getAllClassification() {
        //一次性获取所有数据
        List<ClassificationSimpleData> articleClassificationList = MAPPER.classify.getAllClassification();
        List<ArticleSimpleData> articleSimpleDataList = MAPPER.article.getAllArticleSimpleData();

        //版块列表
        List<Map<String,Object>> sectionList = new ArrayList<>();
        List<List<Map<String,Object>>> projectListList = new ArrayList<>();
        List<List<Map<String,Object>>> articleListList = new ArrayList<>();

        //记录 sectionId 和其 projectList 在 projectListList 的位置的对应关系的Map
        Map<Integer,Integer> projectList_pos = new HashMap<>();
        //记录 projectId 和其 articleList 在 articleListList 中的位置对应关系的Map
        Map<Integer,Integer> articleList_pos = new HashMap<>();

        //扫描分类列表
        for (ClassificationSimpleData classification : articleClassificationList) {
            //经过排序，必定先查扫描SECTION,再扫描PROJECT
            if (classification.getClassifyType() == ClassifyTypeEnum.SECTION) {
                //获取即将插入版块列表的位置
                int sectionIdInList = sectionList.size();
                //在版块列表添加版块
                sectionList.add(classification.toReturnMap());
                //将 section_id - projectList位置 对应关系放入
                projectList_pos.put(classification.getId(),sectionIdInList);

                //新建版块列表下的项目列表并放入
                List<Map<String,Object>> projectList = new ArrayList<>();
                sectionList.get(sectionIdInList).put("projectList",projectList);
                projectListList.add(projectList);
            } else {
                //从对应列表获取上级版块在列表中的位置
                int sectionIdInList = projectList_pos.get(classification.getUpperClassify());
                //获取即将插入项目列表的位置
                int projectIdInList = projectListList.get(sectionIdInList).size();
                //将项目插入项目列表
                projectListList.get(sectionIdInList).add(classification.toReturnMap());
                //将 project_id - articleList位置 对应关系放入
                articleList_pos.put(classification.getId(),articleListList.size());

                //新建项目列表下文章列表并放入
                List<Map<String,Object>> articleList = new ArrayList<>();
                projectListList.get(sectionIdInList).get(projectIdInList).put("articleList",articleList);
                articleListList.add(articleList);
            }
        }

        //扫描文章列表并插入
        for (ArticleSimpleData article : articleSimpleDataList) {
            articleListList.get(articleList_pos.get(article.getProjectId())).add(article.toReturnMap());
        }

        return Response.success(sectionList);
    }



}
