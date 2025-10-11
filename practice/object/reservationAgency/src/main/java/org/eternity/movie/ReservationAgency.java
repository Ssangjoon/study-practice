package org.eternity.movie;

import org.eternity.money.Money;

public class ReservationAgency {
    public Reservation reserve(Screening screening, Customer customer, int audienceCount) {
        boolean discountable = checkDiscountable(screening);
        Money fee = calculateFee(screening, discountable, audienceCount);
        return createReservation(screening, customer, audienceCount, fee);
    }

    private boolean checkDiscountable(Screening screening) {
        return screening.getMovie().getDiscountConditions().stream()
            .anyMatch(condition -> isDiscountable(condition, screening));
    }

    private boolean isDiscountable(DiscountCondition condition, Screening screening) {
        if (condition.getType() == DiscountConditionType.PERIOD){
            return isDiscountableByPeriod(condition, screening);
        }
        return isDiscountableBySequence(condition, screening);
    }

    private boolean isDiscountableByPeriod(DiscountCondition condition, Screening screening) {
        return condition.getDayOfWeek() == screening.getWhenScreened().getDayOfWeek()
            && condition.getStartTime().compareTo(screening.getWhenScreened().toLocalTime()) <= 0
            && condition.getEndTime().compareTo(screening.getWhenScreened().toLocalTime()) >= 0;
    }

    private boolean isDiscountableBySequence(DiscountCondition condition, Screening screening) {
        return condition.getSequence() == screening.getSequence();
    }

    private Money calculateFee(Screening screening, boolean discountable,
        int audienceCount) {
        if (discountable) {
            return screening.getMovie().getFee()
                .minus(calculateDiscountedFee(screening.getMovie()))
                .times(audienceCount);
        }

        return  screening.getMovie().getFee();
    }

    private Money calculateDiscountedFee(Movie movie) {
        return switch(movie.getMovieType()) {
            case AMOUNT_DISCOUNT -> calculateAmountDiscountedFee(movie);
            case PERCENT_DISCOUNT -> calculatePercentDiscountedFee(movie);
            case NONE_DISCOUNT -> calculateNoneDiscountedFee(movie);
        };
    }

    private Money calculateAmountDiscountedFee(Movie movie) {
        return movie.getDiscountAmount();
    }

    private Money calculatePercentDiscountedFee(Movie movie) {
        return movie.getFee().times(movie.getDiscountPercent());
    }

    private Money calculateNoneDiscountedFee(Movie movie) {
        return movie.getFee();
    }

    private Reservation createReservation(Screening screening,
        Customer customer, int audienceCount, Money fee) {
        return new Reservation(customer, screening, fee, audienceCount);
    }
}
