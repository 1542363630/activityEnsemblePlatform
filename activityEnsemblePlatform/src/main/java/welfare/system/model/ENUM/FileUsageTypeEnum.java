package welfare.system.model.ENUM;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FileUsageTypeEnum {
    Article(0),
    Avatar(1),
    Cover(2),
    CLASSIFY_COVER(3),
    CAROUSEL_PHOTO(4);

    public final int id;

    public static FileUsageTypeEnum getType(int typeId) {
        return switch (typeId) {
            case 0 -> Article;
            case 1 -> Avatar;
            case 2 -> Cover;
            case 3 -> CLASSIFY_COVER;
            case 4 -> CAROUSEL_PHOTO;
            default -> throw new IllegalStateException("Unexpected value: " + typeId + ". 请检查数据库!");
        };
    }
}
