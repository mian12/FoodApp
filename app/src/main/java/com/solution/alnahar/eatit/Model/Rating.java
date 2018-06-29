package com.solution.alnahar.eatit.Model;

public class Rating {

    private String userPhone;
    private String foodId;
    private String comment;
    private String ratingValue;

    public  Rating()
    {
        
    }

    public Rating(String userPhone, String foodId, String ratingValue, String comment) {
        this.userPhone = userPhone;
        this.foodId = foodId;
        this.comment = comment;
        this.ratingValue = ratingValue;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }
}
