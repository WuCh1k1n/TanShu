package com.wuch1k1n.tanshu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wuch1k1n.tanshu.model.Book;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
    private Button bt_isCollected;
    private Button bt_buy;

    private Book mbook;
    private List<Book> collectedBooks;

    private Boolean isCollected = false;

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
        bt_isCollected = findViewById(R.id.bt_isCollected);
        bt_buy = findViewById(R.id.bt_buy);

        // 获悉是否从主页面跳转到该页面
        Intent intent = getIntent();
        final Boolean fromHome = intent.getBooleanExtra("from_home", false);

        fab_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromHome) {
                    mbook.save();
                    bt_isCollected.setText("取消收藏");
                    Toast.makeText(BookDetailActivity.this, "已收藏" + "《" +
                            mbook.getTitle() + "》", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    if (isCollected) {
                        Toast.makeText(BookDetailActivity.this, "已收藏" + "《" +
                                mbook.getTitle() + "》", Toast.LENGTH_SHORT).show();
                    } else {
                        mbook.save();
                        bt_isCollected.setText("取消收藏");
                        Toast.makeText(BookDetailActivity.this, "已收藏" + "《" +
                                mbook.getTitle() + "》", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        fab_unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromHome) {
                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                    if (isCollected) {
                        DataSupport.deleteAll(Book.class, "title = ?", mbook.getTitle());
                        bt_isCollected.setText("收藏");
                        Toast.makeText(BookDetailActivity.this, "已取消收藏" + "《" +
                                mbook.getTitle() + "》", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        bt_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BookDetailActivity.this, BuyActivity.class);
                intent1.putExtra("web_url", Utility.getBuyUrl(mbook.getOnline()));
                Log.d("Test", Utility.getBuyUrl(mbook.getOnline()));
                startActivity(intent1);
            }
        });

        mbook = (Book) getIntent().getSerializableExtra("book");
        // 判断是否已经收藏该书
        collectedBooks = DataSupport.findAll(Book.class);
        for (Book book : collectedBooks) {
            if (book.getTitle().equals(mbook.getTitle())) {
                bt_isCollected.setText("取消收藏");
                isCollected = true;
            }
        }
        bt_isCollected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollected) {
                    DataSupport.deleteAll(Book.class, "title = ?", mbook.getTitle());
                    bt_isCollected.setText("收藏");
                    Toast.makeText(BookDetailActivity.this, "已取消收藏" + "《" +
                            mbook.getTitle() + "》", Toast.LENGTH_SHORT).show();
                } else {
                    mbook.save();
                    bt_isCollected.setText("取消收藏");
                    Toast.makeText(BookDetailActivity.this, "已收藏" + "《" +
                            mbook.getTitle() + "》", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
