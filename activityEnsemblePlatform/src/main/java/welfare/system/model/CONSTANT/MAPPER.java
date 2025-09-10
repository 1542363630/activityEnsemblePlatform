package welfare.system.model.CONSTANT;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import welfare.system.mapper.*;

@Component
public class MAPPER implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        user = userMapper;
        file = fileMapper;

        classify = articleClassificationMapper;

        article = articleMapper;
        news = newsMapper;
        achieve = achievementMapper;
        activity = activityMapper;
        train = trainResourceMapper;
    }

    @Autowired
    protected UserMapper userMapper;
    public static UserMapper user;

    @Autowired
    protected FileMapper fileMapper;
    public static FileMapper file;

    @Autowired
    protected ArticleClassificationMapper articleClassificationMapper;
    public static ArticleClassificationMapper classify;

    @Autowired
    protected ArticleMapper articleMapper;
    public static ArticleMapper article;

    @Autowired
    protected NewsMapper newsMapper;
    public static NewsMapper news;

    @Autowired
    protected AchievementMapper achievementMapper;
    public static AchievementMapper achieve;

    @Autowired
    protected ActivityMapper activityMapper;
    public static ActivityMapper activity;

    @Autowired
    protected TrainResourceMapper trainResourceMapper;
    public static TrainResourceMapper train;

}
