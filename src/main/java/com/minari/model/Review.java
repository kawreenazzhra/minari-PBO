package com.minari.model;
import java.util.Date;

public class Review {
    private int reviewID;
    private Customer customer;
    private Product product;
    private double rating;
    private String comment;
    private Date reviewDate;

    public Review(Customer customer, Product product, int rating, String comment){
        this.customer = customer;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = new Date();
    }

    public double getRating(){
        return rating;
    }

    public String getComment(){
        return comment;
    }
}
