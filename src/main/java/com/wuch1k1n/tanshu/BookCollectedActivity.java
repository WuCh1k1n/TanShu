package com.wuch1k1n.tanshu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wuch1k1n.tanshu.model.Book;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class BookCollectedActivity extends AppCompatActivity {

    private ListView lv_book_collected;
    private BookAdapter mAdapter;
    private List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_collected);

        lv_book_collected = findViewById(R.id.lv_book_collected);
        mAdapter = new BookAdapter(this, R.layout.item_book, books);
        lv_book_collected.setAdapter(mAdapter);

        // 从数据库获取已收藏图书
        getCollectedBook();

        lv_book_collected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = (Book) mAdapter.getItem(position);
                Intent intent = new Intent(BookCollectedActivity.this, BookDetailActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });
    }

    private void getCollectedBook() {
        List<Book> results = DataSupport.findAll(Book.class);
        for (Book book : results) {
            books.add(book);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        books.clear();
        getCollectedBook();
    }

}
