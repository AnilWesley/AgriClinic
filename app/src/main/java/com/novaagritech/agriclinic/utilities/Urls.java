package com.novaagritech.agriclinic.utilities;

import android.content.Context;

public class Urls {



    private static String MAIN_URL = "https://www.agriclinic.org/admin/app/ws/";

    public static String IMAGE_URL = "https://www.agriclinic.org/admin/storage/articles/";
    public static String IMAGE_URL1 = "https://www.agriclinic.org/admin/";
    public static String CROP_URL = "https://www.agriclinic.org/admin/app/ws/take_crop_problem";
    public static String CROP_URL1 = "https://www.agriclinic.org/admin/app/ws/crop_problems_list";

    public static String REGISTER_USER=MAIN_URL+"register_user";

    public static String UPDATE_PASSWORD=MAIN_URL+"update_password";

    public static String LOGIN_USER=MAIN_URL+"user_login";

    public static String FORGOT_PASSWORD=MAIN_URL+"forgot_password";

    public static String GET_CROPS_CATEGORY=MAIN_URL+"main_categories";

    public static String GET_CROPS_SUBCATEGORY=MAIN_URL+"sub_categories";

    public static String CROP_LIST=MAIN_URL+"crops_list";

    public static String ARTICLE_LIST=MAIN_URL+"articles";

    public static String ARTICLE_DETAILS=MAIN_URL+"article_details";

    public static String ARTICLE_LIST_TYPES=MAIN_URL+"article_types";

    public static String BANNER_ADDS=MAIN_URL+"banner_adds";

    public static String NEWS=MAIN_URL+"news";

    public static String NEWS_DETAILS=MAIN_URL+"news_details";

    public static String GOVT_SCHEMES=MAIN_URL+"govt_schemes";

    public static String GOVT_SCHEMES_DETAILS=MAIN_URL+"govtScheme_details";

    public static String EVENTS=MAIN_URL+"events";

    public static String EVENTS_DETAILS=MAIN_URL+"event_details";



    public Urls(Context context) {
    }
}
