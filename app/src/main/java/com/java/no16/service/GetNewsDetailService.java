package com.java.no16.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.java.no16.protos.NewsDetail;
import com.java.no16.protos.NewsException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service providing news detail view with corresponding data.
 */

public class GetNewsDetailService {
    interface NewsDetailHttpService {
        @GET("action/query/detail")
        @ConverterType("NewsDetail")
        Call<NewsDetail> getNewsDetail(@Query("newsId") String newsId);
    }

    private static String SERVICE_NAME = "GetNewsDetailService";

    private static GetNewsDetailService.NewsDetailHttpService newsdetailHttpService;

    /**
     * Initiates GetNewsDetailService.
     * Please execute this method when starts the app.
     * */
    public static void initService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://166.111.68.66:2042/news/")
                .addConverterFactory(new Converter.Factory() {
                    Gson gson = new Gson();

                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(final Type type, final Annotation[] annotations, Retrofit retrofit) {
                        return new Converter<ResponseBody, Object>() {
                            @Override
                            public Object convert(ResponseBody value) throws IOException {
                                try {
                                    return gson.getAdapter(TypeToken.get(type)).fromJson(value.charStream());
                                } finally {
                                    value.close();
                                }
                            }
                        };
                    }
                }).build();
        newsdetailHttpService = retrofit.create(GetNewsDetailService.NewsDetailHttpService.class);
    }

    /** Gets news detail with newsId. */
    public NewsDetail getNewsDetail(String newsId) {
        NewsDetail newsDetail;
        try {
            newsDetail = newsdetailHttpService.getNewsDetail(newsId).execute().body();
        } catch (IOException e) {
            Log.e(NewsException.CONVERT_FROM_STRING_TO_JSON_ERROR, String.format(NewsException.CONVERT_FROM_STRING_TO_JSON_MESSAGE, "getNewsDetail", SERVICE_NAME));
            return null;
        }
        newsDetail.setFavorite(CacheService.getFavorite(newsId));
        newsDetail.separateImageUrlString();
        return newsDetail;
    }

    public void setFavorite(String newsId, boolean favorite) {
        CacheService.setFavorite(newsId, favorite);
    }
}