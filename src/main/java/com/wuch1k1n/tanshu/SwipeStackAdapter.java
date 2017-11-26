package com.wuch1k1n.tanshu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wuch1k1n.tanshu.model.Book;

import java.util.List;

/**
 * Created by Administrator on 2017/11/26.
 */

public class SwipeStackAdapter extends ArrayAdapter<Book> {

    private int resourceId;

    public SwipeStackAdapter(Context context, int resourceId, List<Book> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Book book = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        }

        ImageView imageViewCard = (ImageView) convertView.findViewById(R.id.imageViewCard);
        Picasso.with(getContext()).load(book.getImgUrl()).fit().into(imageViewCard);

        return convertView;
    }
}
