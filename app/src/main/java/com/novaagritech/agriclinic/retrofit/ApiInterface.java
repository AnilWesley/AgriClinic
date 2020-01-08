package com.novaagritech.agriclinic.retrofit;




import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.modals.Comments;
import com.novaagritech.agriclinic.modals.Articles;
import com.novaagritech.agriclinic.modals.Banners;
import com.novaagritech.agriclinic.modals.Crops;
import com.novaagritech.agriclinic.modals.Home;
import com.novaagritech.agriclinic.modals.Stories;
import com.novaagritech.agriclinic.modals.Users;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ApiInterface {


        @Headers("Content-Type: application/json")
        @POST("register_user")
        Call<Users> processRegisterOtp(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("forgot_password")
        Call<Users> processForgotPassword(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("update_password")
        Call<Users> processUpdatePassword(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("user_login")
        Call<Users> processUserLogin(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("main_categories")
        Call<Crops> processCropCategories(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("sub_categories")
        Call<Crops> processCropSubCategories(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("crops_list")
        Call<Crops> processCropLists(@Body JsonObject body);


        @Headers("Content-Type: application/json")
        @POST("banner_adds")
        Call<Banners> processBanners1(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("update_user_token")
        Call<Banners> processToken(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("update_user_pincode")
        Call<Banners> processLocation(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("banner_adds")
        Call<Articles> processBanners(@Body JsonObject body);



        @Headers("Content-Type: application/json")
        @POST("articles")
        Call<Articles> processArticlesList1(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("update_likes")
        Call<Articles> processArticlesLikes(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("update_likes")
        Call<Articles> processArticlesUnLikes(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("article_details")
        Call<Articles> processArticlesDetails(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("privacy_policy")
        Call<Articles> processPrivacyPolicy(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("user_stories")
        Call<Stories> processUserStories(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("news")
        Call<Home> processNewsList(@Body JsonObject body);


        @Headers("Content-Type: application/json")
        @POST("take_crop_problem")
        Call<Home> processTakeCropProblem(@Body JsonObject body);


        @Headers("Content-Type: application/json")
        @POST("crop_problems_list")
        Call<Home> processCropProblemsList(@Body JsonObject body);


        @Headers("Content-Type: application/json")
        @POST("events")
        Call<Home> processEventsList(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("govt_schemes")
        Call<Home> processGovtSchemesList(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("news_details")
        Call<Home> processNewsDetails(@Body JsonObject body);


        @Headers("Content-Type: application/json")
        @POST("about_us")
        Call<Home> processAboutUS(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("govtScheme_details")
        Call<Home> processGovtSchemeDetails(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("event_details")
        Call<Home> processEventsDetails(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("add_comment")
        Call<Comments> processAddComment(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("list_commnets")
        Call<Comments> processListComment(@Body JsonObject body);


        @Headers("Content-Type: application/json")
        @POST("update_stories_views")
        Call<Stories> processStoriesViews(@Body JsonObject body);


}
