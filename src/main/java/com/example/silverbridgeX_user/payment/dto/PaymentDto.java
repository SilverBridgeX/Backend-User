package com.example.silverbridgeX_user.payment.dto;

import lombok.*;

public class PaymentDto {

    @Getter
    @Setter
    @ToString
    public static class Amount {
        private int total;
        private int tax_free;
        private int tax;
        private int point;
        private int discount;
        private int green_deposit;
    }

    @Data
    public static class KakaoReadyResponse {
        private String tid;
        private String next_redirect_app_url;
        private String next_redirect_mobile_url;
        private String next_redirect_pc_url;
        private String android_app_scheme;
        private String ios_app_scheme;
        private String created_at;
    }

    @Getter
    @Setter
    @ToString
    public static class KakaoApproveResponse {
        private String aid;
        private String tid;
        private String cid;
        private String sid;
        private String partner_order_id;
        private String partner_user_id;
        private String payment_method_type;
        private Amount amount;
        private String item_name;
        private String item_code;
        private int quantity;
        private String created_at;
        private String approved_at;
        private String payload;
    }

    @Getter
    @Setter
    @ToString
    public static class KakaoCancelResponse {
        private String aid;
        private String tid;
        private String cid;
        private String status;
        private String partner_order_id;
        private String partner_user_id;
        private String payment_method_type;
        private Amount amount;
        private ApprovedCancelAmount approved_cancel_amount;
        private CanceledAmount canceled_amount;
        private CancelAvailableAmount cancel_available_amount;
        private String item_name;
        private String item_code;
        private int quantity;
        private String created_at;
        private String approved_at;
        private String canceled_at;
        private String payload;
    }

    @Getter
    @Setter
    @ToString
    public static class KakaoSubscribeCancelResponse {
        private String cid;
        private String sid;
        private String status;
        private String created_at;
        private String last_approved_at;
        private String inactivated_at;
    }

    @Getter
    @Setter
    @ToString
    public static class KakaoSubscribeStatusResponse {
        private boolean available;
        private String cid;
        private String sid;
        private String status;
        private String item_name;
        private String payment_method_type;
        private String created_at;
        private String last_approved_at;
        private String use_point_status;
    }

    @Getter
    @Setter
    @ToString
    public static class ApprovedCancelAmount {
        private int total;
        private int tax_free;
        private int vat;
        private int point;
        private int discount;
        private int green_deposit;
    }

    @Getter
    @Setter
    @ToString
    public static class CanceledAmount {
        private int total;
        private int tax_free;
        private int vat;
        private int point;
        private int discount;
        private int green_deposit;
    }

    @Getter
    @Setter
    @ToString
    public static class CancelAvailableAmount {
        private int total;
        private int tax_free;
        private int vat;
        private int point;
        private int discount;
        private int green_deposit;
    }

    @Builder
    @Getter
    @Setter
    @ToString
    public static class KakaoPayStatus {
        private boolean isLogExist;
        private String status;
        private String last_approved_at;
    }
}
