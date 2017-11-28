package com.wuch1k1n.tanshu;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wuch1k1n.tanshu.model.Book;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.Utility;

public class BookDetailActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ImageView iv_book;
    private TextView tv_book_title;
    private TextView tv_book_author;
    private TextView tv_book_publisher;
    private TextView tv_book_rating;
    private TextView tv_book_brief;
    private FloatingActionButton fab_like;
    private FloatingActionButton fab_unlike;

    private Book mbook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        iv_book = findViewById(R.id.iv_book);
        tv_book_title = findViewById(R.id.tv_book_title);
        tv_book_author = findViewById(R.id.tv_book_author);
        tv_book_publisher = findViewById(R.id.tv_book_publisher);
        tv_book_rating = findViewById(R.id.tv_book_rating);
        tv_book_brief = findViewById(R.id.tv_book_brief);
        fab_like = findViewById(R.id.fab_like);
        fab_unlike = findViewById(R.id.fab_unlike);

        fab_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fab_unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mbook = (Book) getIntent().getSerializableExtra("book");
        queryFromDouban();
    }

    // 向豆瓣网查询图书信息
    private void queryFromDouban() {
        // 豆瓣网请求图书信息地址
        String address = "https://api.douban.com/v2/book/search";
        // 请求参数
        Map params = new HashMap();
        // 书名
        params.put("q", mbook.getTitle());
        // 取结果的条数
        params.put("count", 1);
        String url = address + "?" + HttpUtil.urlencode(params);

        // 弹出进度对话框
        showProgressDialog();

        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 关闭进度对话框
                        closeProgressDialog();
                        Toast.makeText(BookDetailActivity.this, "豆瓣网查询失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                // result带有图书作者、评分信息
                Book result = Utility.handleDoubanResponse(responseText);
                mbook.setAuthor(result.getAuthor());
                mbook.setPublisher(result.getPublisher());
                mbook.setRating(result.getRating());
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 关闭进度对话框
                        closeProgressDialog();
                        Picasso.with(BookDetailActivity.this).load(mbook.getImgUrl()).fit().into(iv_book);
                        tv_book_title.setText(mbook.getTitle());
                        tv_book_author.setText("作者：" + mbook.getAuthor());
                        tv_book_publisher.setText("出版社：" + mbook.getPublisher());
                        tv_book_rating.setText("豆瓣评分：" + mbook.getRating());
                        tv_book_brief.setText(mbook.getBrief());
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
