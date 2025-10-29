package com.minari.model;

import java.util.Date;

public class Payment {
    private int paymentID;
    private Order order;
    private double amount;
    private String method;
    private String status;
    private Date paymentDate;


    public Payment(Order order, String method){
        this.order = order;
        this.method = method;
    }

    public boolean processPayment(){
        if(amount > 0 && order!= null){
            this.status = "Paid";
            this.paymentDate = new Date();
            System.out.println("Payment successful for order: " + order.getOrderId());
            return true;
        }else{
            this.status = "Failed";
            System.out.println("Payment failed. Invalid amount or order");
            return false;
        }
    }
    public String generateInvoice(){
        return "Invoice#" + paymentID + "- Amount: Rp" + amount + "- Status: " + status;
    }
}

