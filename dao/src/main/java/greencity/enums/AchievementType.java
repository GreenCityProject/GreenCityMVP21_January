package greencity.enums;

public enum AchievementType {

    AQUAINTANCE(1),
    ECO_FRIEND(2),
    CONSCIOUS(3),
    WOW(4),
    WELL_DONE(5),
    TAKE_FIVE(6),
    GURU(7),
    SENSEI(8),
    SPACE(9);

    private final int rank;

    AchievementType(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
