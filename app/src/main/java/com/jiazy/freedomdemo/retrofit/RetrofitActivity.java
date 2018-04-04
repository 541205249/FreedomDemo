package com.jiazy.freedomdemo.retrofit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jiazy.freedomdemo.R;
import com.jiazy.freedomdemo.retrofit.base.Fault;
import com.jiazy.freedomdemo.retrofit.bean.Movie;

public class RetrofitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        setTitle("Retrofit");

        executeHttp();
    }

    private void executeHttp() {
        MovieLoader movieLoader = new MovieLoader();
        movieLoader.getMovie(0, 10).subscribe(movies -> {
            StringBuilder sb = new StringBuilder();
            for (Movie movie :movies) {
                Log.i("jzy", movie.getTitle());
                Log.i("jzy", movie.getOriginal_title());
                Log.i("jzy", movie.getAlt());
                sb.append(movie.getTitle()).append(",\n")
                        .append(movie.getOriginal_title()).append(",\n")
                        .append(movie.getAlt()).append(",\n")
                        .append(movie.getImages().getLarge()).append(",\n");
            }
            showTxt(sb.toString());

        }, throwable -> {
            Log.e("TAG", "error message:" + throwable.getMessage());
            if(throwable instanceof Fault){
                Fault fault = (Fault) throwable;
                if(fault.status == 404){
                    //错误处理
                }else if(fault.status == 500){
                    //错误处理
                }else if(fault.status == 501){
                    //错误处理
                }
            }
        });

    }

    private void showTxt(String txt) {
        TextView textView = findViewById(R.id.textview1);
        textView.setText(txt);
    }

}
