package com.wuch1k1n.tanshu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
    private static final int REQUEST_BOOK_DETAIL = 1;
    private FloatingActionButton mButtonLeft, mButtonRight;

    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mAdapter;
    private List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeStack = findViewById(R.id.swipeStack);
        mButtonLeft = findViewById(R.id.buttonSwipeLeft);
        mButtonRight = findViewById(R.id.buttonSwipeRight);

        mButtonLeft.setOnClickListener(this);
        mButtonRight.setOnClickListener(this);

        books = new ArrayList<>();
        mAdapter = new SwipeStackAdapter(MainActivity.this, R.layout.card, books);
        mSwipeStack.setAdapter(mAdapter);
        mSwipeStack.setListener(this);

        queryFromJuhe(APPKEY, 246, 10, 10);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mButtonLeft)) {
            mSwipeStack.swipeTopViewToLeft();
        } else if (v.equals(mButtonRight)) {
            mSwipeStack.swipeTopViewToRight();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuBookList:
                Intent intent = new Intent(this, BookCollectedActivity.class);
                startActivity(intent);
                break;
            case R.id.menuSet:
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onViewClicked(int position) {
        Book book = mAdapter.getItem(position);
        Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
        intent.putExtra("from_home", true);
        intent.putExtra("book", book);
        startActivityForResult(intent, REQUEST_BOOK_DETAIL);
    }

    @Override
    public void onViewSwipedToRight(int position) {
        Book mbook = mAdapter.getItem(position);
        // 向豆瓣网查询图书详细信息
        queryFromDoubanAndSave(mbook);
        Toast.makeText(MainActivity.this, "已收藏" + "《" +
                mbook.getTitle() + "》", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewSwipedToLeft(int position) {

    }

    @Override
    public void onStackEmpty() {

    }

    @Override
    public void onStackLeftOne() {
        queryFromJuhe(APPKEY, 246, 21, 5);
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
                List<Book> results = Utility.handleJuheResponse(responseText, length);
                //books.clear();
                for (Book book : results) {
                    books.add(book);
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
    private void queryFromDoubanAndSave(final Book book) {
        // 豆瓣网请求图书信息地址
        final String address = "https://api.douban.com/v2/book/search";
        // 请求参数
        Map params = new HashMap();
        // 书名
        params.put("q", book.getTitle());
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

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Book mbook = book;
                String responseText = response.body().string();
                // result带有图书作者、评分信息
                Book result = Utility.handleDoubanResponse(responseText);
                mbook.setAuthor(result.getAuthor());
                mbook.setPublisher(result.getPublisher());
                mbook.setRating(result.getRating());
                mbook.save();
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_BOOK_DETAIL:
                if (resultCode == RESULT_CANCELED) {
                    mSwipeStack.removeTopView();
                }
                if (resultCode == RESULT_OK) {
                    mSwipeStack.removeTopView();
                }
                break;
            default:
        }
    }
}
