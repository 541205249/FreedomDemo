package com.jiazy.freedomdemo.retrofit;

import com.jiazy.freedomdemo.retrofit.base.ObjectLoader;
import com.jiazy.freedomdemo.retrofit.base.RetrofitServiceManager;
import com.jiazy.freedomdemo.retrofit.bean.Movie;
import com.jiazy.freedomdemo.retrofit.bean.MovieSubject;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;

/**
 * 作者： jiazy
 * 日期： 2018/3/1.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class MovieLoader extends ObjectLoader {
    private MovieService mMovieService;
    public MovieLoader(){
        mMovieService = RetrofitServiceManager.getInstance().create(MovieService.class);
    }

    public Observable<List<Movie>> getMovie(int start, int count){
        return observe(mMovieService.getTop250(start,count))
                .map(MovieSubject::getSubjects);

//        return observe(mMovieService.getTop250(start,count))
//                .map(new PayLoad<BaseResponse<List<Movie>>>());
    }

    public Observable<String> getWeatherList(String cityId,String key){
        return observe(mMovieService.getWeather(cityId,key))
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        //可以处理对应的逻辑后在返回
                        return s;
                    }
                });
    }

    public interface MovieService{
        //获取豆瓣Top250 榜单
        @GET("top250")
        Observable<MovieSubject> getTop250(@Query("start") int start, @Query("count") int count);

        @FormUrlEncoded
        @POST("/x3/weather")
        Observable<String> getWeather(@Field("cityId") String cityId, @Field("key") String key);
    }
}
