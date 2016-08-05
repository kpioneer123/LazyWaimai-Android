package net.comet.lazyorder.util;

public class Constants {

    public class Persistence {
        public static final String USER_INFO = "app.lazywaimai.userinfo";
        public static final String LAST_LOGIN_PHONE = "app.lazywaimai.phone";
        public static final String ACCESS_TOKEN = "app.lazywaimai.token";
        public static final String SHARE_FILE = "app.lazywaimai.share";
    }

    public class Code {
        public static final int HTTP_UNAUTHORIZED = 401;
        public static final int HTTP_SERVER_ERROR = 500;
        public static final int HTTP_NOT_HAVE_NETWORK = 600;
        public static final int HTTP_NETWORK_ERROR = 700;
        public static final int HTTP_UNKNOWN_ERROR = 800;
    }

    public class Path {
        public static final String USER_LOGIN = "user/login";
        public static final String USER_REGISTER = "user/create";

        public static final String BUSINESS_RESTAURANTS = "business/restaurants";
        public static final String BUSINESS_STORES = "business/stores";
        public static final String BUSINESS_DRINKS = "business/drinks";
        public static final String BUSINESS_DETAIL = "business/business";
        public static final String BUSINESS_PRODUCT = "product/products";

        public static final String COMMON_SEND_SMS_CODE = "common/sendSMSCode";
    }

    public class Key {
        public static final String HEADER_CONTENT_TYPE = "Content-Type";
        public static final String HEADER_AUTHORIZATION = "Authorization";
        public static final String HEADER_HTTP_TIMESTAMP = "Http-Timestamp";
        public static final String HEADER_HTTP_APP_VERSION = "Http-App-Version";
        public static final String HEADER_HTTP_DEVICE_ID = "Http-Device-Id";
        public static final String HEADER_HTTP_DEVICE_TYPE = "Http-Device-Type";

        public static final String SP_COOKIE_FILE = "cookie_file";
        public static final String SP_USER_INFO = "user_info";
        public static final String SP_LOGIN_STATE = "login_state";
        public static final String SP_TOKEN = "access_token";
    }

    public class Page {
        public static final String PAGE_LOGIN = "user_login";
        public static final String PAGE_ADDRESS_CHOOSE = "address_choose";
        public static final String PAGE_ADDRESS_LIST = "address_list";
    }

    public class ClickType {
        public static final int CLICK_TYPE_DELETE_BTN_CLICKED = 1001;
        public static final int CLICK_TYPE_EDIT_BTN_CLICKED = 1002;
        public static final int CLICK_TYPE_BUSINESS_CLICKED = 1003;
        public static final int CLICK_TYPE_ORDER_CLICKED = 1004;
        public static final int CLICK_TYPE_ADDRESS_CLICKED = 1005;
        public static final int CLICK_TYPE_PRODUCT_CATEGORY_CLICKED = 1006;
        public static final int CLICK_TYPE_SHOPPING_CART_CLICKED = 1007;
    }
}
