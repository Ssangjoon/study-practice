package org.eternity.movie;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class DiscountCondition {
    private DiscountConditionType type;

    private int sequence;

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public boolean isDiscountable(Screening screening) {
        if (type == DiscountConditionType.PERIOD){
            return isDiscountableByPeriod(screening);
        }
        return isDiscountableBySequence(screening);
    }

    private boolean isDiscountableByPeriod(Screening screening) {
        return dayOfWeek == screening.getWhenScreened().getDayOfWeek()
            && startTime.compareTo(screening.getWhenScreened().toLocalTime()) <= 0
            && endTime.compareTo(screening.getWhenScreened().toLocalTime()) >= 0;
    }

    private boolean isDiscountableBySequence(Screening screening) {
        return sequence == screening.getSequence();
    }

    public DiscountConditionType getType() {
        return type;
    }

    public void setType(DiscountConditionType type) {
        this.type = type;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
