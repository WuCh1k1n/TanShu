package util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.wuch1k1n.tanshu.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/25.
 */

public class Utility {

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static List<Book> handleJuheResponse(String response, int length) {
        List<Book> books = new ArrayList<>();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getInt("resultcode") == 200) {
                    JSONObject resultObject = jsonObject.getJSONObject("result");
                    JSONArray dataArray = resultObject.getJSONArray("data");
                    for (int i = 0; i < length; i++) {
                        JSONObject bookObject = dataArray.getJSONObject(i);
                        Book book = new Book();
                        book.setTitle(bookObject.getString("title"));
                        book.setBrief(bookObject.getString("sub2"));
                        book.setImgUrl(bookObject.getString("img"));
                        book.setOnline(bookObject.getString("online"));
                        books.add(book);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return books;
    }

    public static Book handleDoubanResponse(String response) {
        Book book = new Book();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray booksArray = jsonObject.getJSONArray("books");
                JSONObject tempObject = booksArray.getJSONObject(0);
                JSONObject ratingObject = tempObject.getJSONObject("rating");
                book.setRating(ratingObject.getDouble("average"));
                JSONArray authorArray = tempObject.getJSONArray("author");
                book.setAuthor(authorArray.getString(0));
                book.setPublisher(tempObject.getString("publisher"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return book;
    }

    public static String getBuyUrl(String online) {
        int start = online.indexOf("h");
        int end = online.indexOf("l", start);
        return online.substring(start, end + 1);
    }
}
