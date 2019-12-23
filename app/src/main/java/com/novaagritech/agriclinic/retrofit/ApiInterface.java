package com.novaagritech.agriclinic.retrofit;




import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.comments.FriendlyMessage;
import com.novaagritech.agriclinic.modals.ArticlesData;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.BannerData;
import com.novaagritech.agriclinic.modals.CropsData;
import com.novaagritech.agriclinic.modals.SchemesData;
import com.novaagritech.agriclinic.modals.Stories;
import com.novaagritech.agriclinic.modals.UserData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ApiInterface {


        @Headers("Content-Type: application/json")
        @POST("register_user")
        Call<UserData> processRegisterOtp(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("forgot_password")
        Call<UserData> processForgotPassword(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("update_password")
        Call<UserData> processUpdatePassword(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("user_login")
        Call<UserData> processUserLogin(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("main_categories")
        Call<CropsData> processCropCategories(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("sub_categories")
        Call<CropsData> processCropSubCategories(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("crops_list")
        Call<CropsData> processCropLists(@Body JsonObject body);


        @Headers("Content-Type: application/json")
        @POST("banner_adds")
        Call<BannerData> processBanners1(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("banner_adds")
        Call<ArticlesList> processBanners(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("article_types")
        Call<ArticlesData> processArticlesTypes(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("articles")
        Call<ArticlesList> processArticlesList1(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("update_likes")
        Call<ArticlesList> processArticlesLikes(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("update_likes")
        Call<ArticlesList> processArticlesUnLikes(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("article_details")
        Call<ArticlesList> processArticlesDetails(@Body JsonObject body);


        @Headers("Content-Type: application/json")
        @POST("user_stories")
        Call<Stories> processUserStories(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("news")
        Call<SchemesData> processNewsList(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("events")
        Call<SchemesData> processEventsList(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("govt_schemes")
        Call<SchemesData> processGovtSchemesList(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("news_details")
        Call<SchemesData> processNewsDetails(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("govtScheme_details")
        Call<SchemesData> processGovtSchemeDetails(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("event_details")
        Call<SchemesData> processEventsDetails(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("add_comment")
        Call<FriendlyMessage> processAddComment(@Body JsonObject body);

        @Headers("Content-Type: application/json")
        @POST("list_commnets")
        Call<FriendlyMessage> processListComment(@Body JsonObject body);


        @Headers("Content-Type: application/json")
        @POST("update_stories_views")
        Call<Stories> processStoriesViews(@Body JsonObject body);


}
