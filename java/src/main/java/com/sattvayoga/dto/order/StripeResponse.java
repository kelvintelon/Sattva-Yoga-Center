package com.sattvayoga.dto.order;

public class StripeResponse {
    private String sessionId;
    private String paymentId;

    public StripeResponse(String sessionId,String paymentId) {
        this.sessionId = sessionId;
        this.paymentId = paymentId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
