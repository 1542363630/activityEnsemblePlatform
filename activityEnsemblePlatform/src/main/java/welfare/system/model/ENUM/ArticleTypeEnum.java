package welfare.system.model.ENUM;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ArticleTypeEnum {
    ACHIEVEMENT(1),
    ACTIVITY(2),
    TRAIN(3),
    NEWS(4);

    public final int id;

    public static ArticleTypeEnum getType(int typeId) {
        return switch (typeId) {
            case 1 -> ACHIEVEMENT;
            case 2 -> ACTIVITY;
            case 3 -> TRAIN;
            case 4 -> NEWS;
            default -> throw new IllegalStateException("Unexpected value: " + typeId + ". 请检查数据库!");
        };
    }
}
