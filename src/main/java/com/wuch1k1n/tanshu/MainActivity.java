package com.wuch1k1n.tanshu;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wuch1k1n.tanshu.model.Book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.Utility;

public class MainActivity extends AppCompatActivity implements SwipeStack.SwipeStackListener, View.OnClickListener {

    private static final String APPKEY = "133347f7868e963049602673bc00896c";
    private Button mButtonLeft, mButtonRight;
    private FloatingActionButton mFab;

    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mAdapter;
    private List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeStack = (SwipeStack) findViewById(R.id.swipeStack);
        mButtonLeft = (Button) findViewById(R.id.buttonSwipeLeft);
        mButtonRight = (Button) findViewById(R.id.buttonSwipeRight);
        mFab = (FloatingActionButton) findViewById(R.id.fabAdd);

        mButtonLeft.setOnClickListener(this);
        mButtonRight.setOnClickListener(this);
        mFab.setOnClickListener(this);

        books = new ArrayList<>();
        mAdapter = new SwipeStackAdapter(MainActivity.this, R.layout.card, books);
        mSwipeStack.setAdapter(mAdapter);
        mSwipeStack.setListener(this);

        queryFromJuhe(APPKEY, 246, 10, 5);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mButtonLeft)) {
            mSwipeStack.swipeTopViewToLeft();
        } else if (v.equals(mButtonRight)) {
            mSwipeStack.swipeTopViewToRight();
        } else if (v.equals(mFab)) {

        }
    }

    @Override
    public void onViewClicked(int position) {
        Toast.makeText(this, "点击了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewSwipedToRight(int position) {
        Toast.makeText(this, "右划了",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewSwipedToLeft(int position) {
        Toast.makeText(this, "左划了",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStackEmpty() {
        Toast.makeText(this, R.string.stack_empty, Toast.LENGTH_SHORT).show();
    }

    private void queryFromJuhe(String key, int catalogId, int startNum, final int length) {
        // 聚合数据请求图书内容地址
        String address = "http://apis.juhe.cn/goodbook/query";
        // 请求参数
        Map params = new HashMap();
        // 应用APPKEY
        params.put("key", key);
        // 目录编号
        params.put("catalog_id", catalogId);
        // 数据返回起始
        params.put("pn", startNum);
        // 数据返回条数，最大30
        params.put("rn", length);
        String url = address + "?" + HttpUtil.urlencode(params);

        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "聚合数据查询失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                List<Book> responseList = Utility.handleJuheResponse(responseText, length);
                for (Book book : responseList) {
                    books.add(book);
                }
                for (int i = 0; i < length; i++) {
                    //queryFromDouban(i);
                }
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    // 向豆瓣网查询图书信息
    private void queryFromDouban(final int i) {
        // 豆瓣网请求图书信息地址
        String address = "https://api.douban.com/v2/book/search";
        // 请求参数
        Map params = new HashMap();
        // 书名
        params.put("q", books.get(i).getName());
        // 取结果的条数
        params.put("count", 1);
        String url = address + "?" + HttpUtil.urlencode(params);

        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "豆瓣网查询失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                // result带有图书作者、评分信息
                Book result = Utility.handleDoubanResponse(responseText);
                books.get(i).setAuthor(result.getAuthor());
                books.get(i).setRating(result.getRating());
                Log.d("Test", books.get(i).getName());
                Log.d("Test", books.get(i).getAuthor());
                Log.d("Test", books.get(i).getBrief());
                Log.d("Test", books.get(i).getImgUrl());
                Log.d("Test", String.valueOf(books.get(i).getRating()));
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
