package welfare.system.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.CONSTANT.VALUE;
import welfare.system.model.ENUM.ArticleTypeEnum;
import welfare.system.model.dto.ArticlePostData;
import welfare.system.model.dto.page.PageData;
import welfare.system.model.dto.result.PageResultData;
import welfare.system.model.dto.search.ActivitySearchData;
import welfare.system.model.po.ActivityInfo;
import welfare.system.model.po.User;
import welfare.system.model.vo.CommonErr;
import welfare.system.model.vo.Response;
import welfare.system.util.FileUtil;

import java.util.*;

@Service
public class ActivityService {

    //发布活动
    public Response postActivity(HttpServletRequest request, ArticlePostData<ActivityInfo> articlePostData){
        ActivityInfo activityInfo = articlePostData.getArticle();

        //设置文章发布者uid
        activityInfo.setPostUid(((User) request.getAttribute("user")).getUid());
        //设置文章类型
        activityInfo.setTypeEnum(ArticleTypeEnum.ACTIVITY);

        // 将article存入数据库
        int articleId;
        try {
            articleId = MAPPER.article.postArticle(activityInfo);
        }catch (RuntimeException e){
            return Response.failure(400, "文章上传失败了QwQ");
        }

        // 将 activity 和引用文件存入数据库
        activityInfo.setArticleId(articleId);
        try{
            int id = MAPPER.activity.postActivity(activityInfo);
            try {
                //引用关系存入数据库
                FileUtil.recordQuote(articleId, articlePostData.getFileQuote());
            } catch (RuntimeException e) {
                //出错，删除news
                MAPPER.activity.removeActivityById(id);
                System.out.println(e.getMessage());
                return Response.failure(400,"请检查文件引用是否正确!");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            MAPPER.article.deleteArticleById(articleId);
            return Response.failure(400,"文章上传失败了QwQ");
        }

        return Response.ok();
    }

    //获取个人活动列表(分页)
    public Response selectActivityByUserId(Integer uid,Integer page,Integer pageSize){
        try {
            if (pageSize == null) pageSize = 5;
            if(page<=0) return Response.failure(404,"页数只能为正数");
            int totalNum = MAPPER.activity.selectNumberOfActivityByUid(uid);
            //得到总页数
            int maxPage = (int) Math.ceil((double) totalNum / pageSize);
            if(page>maxPage){
                return Response.failure(404,"页数超过最大页数。最大页数：" + maxPage);
            }
            //得到指定页
            List<Map<String,Object>> activityInfoList = MAPPER.activity.selectActivityByUserId(uid,pageSize,(page -1)* pageSize);
            for (Map<String,Object> m : activityInfoList) {
                m.put("coverURL", VALUE.web_path + VALUE.img_web + m.get("coverURL"));
                m.put("ongoing",((long) m.get("ongoing")) == 0);
            }

            return Response.success(new PageResultData<>(maxPage,totalNum,activityInfoList).toReturnMapExceptClassificationList());
        }catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return Response.failure(400,"查询失败");
        }
    }

    //活动筛选（按 关键词，是否进行中，项目类型 进行筛选）（分页）
    public Response selectActivityConstrained(ActivitySearchData activitySearchData){
        PageData pageData = activitySearchData.getPageData();
        try{
            if (pageData.getNum() == null) pageData.setNum(5);
            if(pageData.getPage()<=0) return Response.failure(404,"页数只能为正数");
            String sql = activitySearchData.getSearchSql();
            Integer totalNum = MAPPER.activity.selectNumberOfActivityConstrained(sql);
            //得到总页数
            int maxPage = (int) Math.ceil((double) totalNum / pageData.getNum());
            if(pageData.getPage()>maxPage){
                return Response.failure(404,"页数超过最大页数。最大页数：" + maxPage);
            }
            List<Map<String,Object>> activityList = MAPPER.activity.selectActivityConstrained(sql,pageData.getNum(),(pageData.getPage() - 1) * pageData.getNum());
            for (Map<String,Object> m : activityList) {
                m.put("coverURL", VALUE.web_path + VALUE.img_web + m.get("coverURL"));
            }

            return Response.success(new PageResultData<>(maxPage,totalNum,activityList).toReturnMapExceptClassificationList());
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return Response.failure(400,"查询失败o(╥﹏╥)o");
        }
    }


    //获取活动预览列表
    public Response previewActivity(Integer num) {
        //不应该超过5
        num = num == null || num < 0 || num > 5 ? 3 : num;

        List<Map<String,Object>> activityList = MAPPER.activity.selectPreviewActivity(num);
        if (activityList == null || activityList.isEmpty()) {
            return Response.failure(CommonErr.NO_DATA);
        }

        for (Map<String,Object> m : activityList) {
            m.put("coverURL",VALUE.web_path + VALUE.img_web + m.get("coverURL"));
            m.put("ongoing",((Integer) m.get("ongoing")) == 0);
        }
        return Response.success(activityList);
    }

    //获取报名未结束活动列表，按板块分类
    public Response selectActivityBySection(int sectionId){
        try {
            //拿到所有数据
            List<Map<String,Object>> activityList = MAPPER.activity.selectActivityOngoingBySection(sectionId);
            if (activityList == null || activityList.isEmpty()) {
                return Response.failure(CommonErr.NO_DATA);
            }

            List<Map<String,Object>> resultList = new ArrayList<>();  //返回列表
            Map<Integer,List<Map<String,Object>>> activityInProjectListMap = new HashMap<>();  //项目id-活动 对应表
            //获取的数据已经按classify_rank排序，固加入列表的数据也是按此顺序插入
            for(Map<String,Object> m : activityList) {
                Integer projectId = (Integer) m.get("projectId");
                //若事先不存在该projectId
                if (activityInProjectListMap.get(projectId) == null) {
                    //新建相同项目下的活动列表
                    List<Map<String,Object>> activitySimpleList = new ArrayList<>();
                    //将新项目活动列表放入 项目id-活动 对应表
                    activityInProjectListMap.put(projectId,activitySimpleList);
                    //新建项目
                    Map<String,Object> resultMap = new HashMap<>();
                    //将新项目插入resultList
                    resultList.add(resultMap);
                    //将数据插入新建的resultList
                    resultMap.put("projectName",m.get("projectName"));
                    resultMap.put("projectId",projectId);
                    resultMap.put("activityList",activitySimpleList);
                }
                //将获取到的activityMap插入对应的activitySimpleList
                activityInProjectListMap.get(projectId).add(m);
                //将activityMap转化为只包含需要数据的activitySimpleMap
                m.put("coverURL",VALUE.web_path + VALUE.img_web + m.get("coverURL"));
                m.remove("projectName");
                m.remove("projectId");
            }

            return Response.success(resultList);
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return Response.failure(401,"查询出错:");
        }
    }

//    //获取报名未结束活动列表（分页）  不在返回已结束，未结束按版块分，将不同项目放一个列表返回，不再分页
//    public Response selectActivityOngoing(Integer page){
//        try {
//            int pageSize = 5;
//            if(page<=0) return Response.failure(404,"页数只能为正数");
//            //得到总页数
//            int total = MAPPER.activity.selectNumberOfActivityOngoing();
//            int maxPage = (int) Math.ceil((double) total / pageSize);
//            if(page>maxPage){
//                return Response.failure(404,"页数超过最大页数。最大页数：" + maxPage);
//            }
//            //得到指定页
//            List<ActivityInfo> activityInfoList = MAPPER.activity.selectActivityOngoing(pageSize,(page -1)* pageSize);
//            PageResultData<ActivityInfo> pageResultData = new PageResultData<>();
//            pageResultData.setPageNumber(maxPage);
//            pageResultData.setTotalNum(total);
//            pageResultData.setList(activityInfoList);
//
//            return Response.success(pageResultData);
//        }catch (RuntimeException e){
//            return Response.failure(400,"查询失败");
//        }
//    }
//
//    //获取报名已结束活动列表（分页）
//    public Response selectActivityEnd(Integer page){
//        try {
//            int pageSize = 5;
//            if(page<=0) return Response.failure(404,"页数只能为正数");
//            //得到总页数
//            int maxPage = (int) Math.ceil((double) MAPPER.activity.selectNumberOfActivityEnd() / pageSize);
//            if(page>maxPage){
//                return Response.failure(404,"页数超过最大页数。最大页数：" + maxPage);
//            }
//            //得到指定页
//            List<ActivityInfo> activityInfoList = MAPPER.activity.selectActivityEnd(pageSize,(page -1)* pageSize);
//            PageResultData<ActivityInfo> pageResultData = new PageResultData<>();
//            pageResultData.setPageNumber(maxPage);
//            pageResultData.setList(activityInfoList);
//
//            return Response.success(pageResultData);
//        }catch (RuntimeException e){
//            return Response.failure(400,"查询失败");
//        }
//    }

    //根据id查看活动
    public Response selectActivityById(Integer uid,Integer id){
        try {
            ActivityInfo activityInfo = MAPPER.activity.selectActivityById(id);
            //获取报名状态
            Integer registerStatus = MAPPER.activity.getRegisterStatus(uid,id);
            Map<String,Object> returnMap = activityInfo.toReturnMap();
            //如果已报名则返回联系方式 //ToDo:返回QRcode
            if (registerStatus!= null && registerStatus == 0) {
                returnMap.put("contactImageId",activityInfo.getContactImageId());
            }
            return Response.success(returnMap);
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return Response.failure(400,"查询失败o(╥﹏╥)o");
        }
    }

    //申请参加活动
    public Response registerActivity(Integer uid,Integer id){
        try {
            ActivityInfo activityInfo = MAPPER.activity.selectActivityById(id);
            if (activityInfo == null) {
                return Response.failure(400,"你要申请的活动不存在!");
            }

            Date nowDate = new Date(System.currentTimeMillis());
            //若报名人数已超过报名上限
            if(activityInfo.getRegisterNum() >= activityInfo.getQuota()){
                return Response.failure(401,"申请满了QWQ");
            }
            //若报名时间已超过报名截止时间
            else if(nowDate.after(activityInfo.getRegisterEndTime())){
                return Response.failure(401,"报名已经结束喽~~");
            }
            //若报名时间早于报名开始时间
            else if(nowDate.before(activityInfo.getRegisterStartTime())) {
                return Response.failure(401,"报名还未开始呢!");
            }

            //获取报名状态
            Integer registerStatus = MAPPER.activity.getRegisterStatus(uid,id);
            //没有该报名数据，表明先前没有报名过
            if (registerStatus == null) {
                //报名成功
                MAPPER.activity.registerActivity(uid, id);
                activityInfo.setContentAsHTML();
                return Response.success(activityInfo.getContactImageId());
            }

            //status为0，表明已报名过
            else if (registerStatus == 0) {
                return Response.failure(401,"已经报名过该活动了呢!");
            }
            //status不为0，可能报名被拒绝，不可再次报名
            else {
                return Response.failure(401,"你的报名被拿下了T_T，有疑惑请反馈哦");
            }
        } catch (RuntimeException e) {
            return Response.failure(400,"申请失败(╥﹏╥)o");
        }
    }



}
