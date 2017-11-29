package com.wuch1k1n.tanshu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wuch1k1n.tanshu.model.Book;

import java.util.List;

/**
 * Created by Administrator on 2017/10/15.
 */

public class BookAdapter extends ArrayAdapter {

    private int resourceId;

    public BookAdapter(@Nullable Context context, @Nullable int resource, @Nullable List objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Book mbook = (Book) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iv_cover = (ImageView) view.findViewById(R.id.iv_cover);
            viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            viewHolder.tv_author = (TextView) view.findViewById(R.id.tv_author);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.with(getContext()).load(mbook.getImgUrl()).fit().into(viewHolder.iv_cover);
        viewHolder.tv_title.setText(mbook.getTitle());
        viewHolder.tv_author.setText(mbook.getAuthor() + "（著）");
        return view;
    }

    class ViewHolder {
        ImageView iv_cover;
        TextView tv_title;
        TextView tv_author;
    }
}
