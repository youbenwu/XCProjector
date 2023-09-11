package com.outmao.xcprojector.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.outmao.xcprojector.R;

import androidx.annotation.Nullable;

public class MenuItemView extends RelativeLayout {

    public View iconView;

    private TextView titleView;

    private TextView subtitleView;

    private String title;
    private String subtitle;


    private void init(){
        iconView=findViewById(R.id.icon);
        titleView=findViewById(R.id.tv_title);
        subtitleView=findViewById(R.id.tv_sub_title);
        update();
    }

    private void update(){
        titleView.setText(title);
        subtitleView.setText(subtitle);
        if(isSelected()){
            titleView.setTextColor(getResources().getColor(R.color.menu_text_color_selected,null));
            subtitleView.setTextColor(getResources().getColor(R.color.menu_text_color_selected,null));
        }else{
            titleView.setTextColor(getResources().getColor(R.color.menu_text_color,null));
            subtitleView.setTextColor(getResources().getColor(R.color.menu_text_color,null));
        }
    }

    @Override
    public void setSelected(boolean selected){
        super.setSelected(selected);
        update();
    }


    public MenuItemView(Context context) {
        super(context);
        inflate(getContext(), R.layout.view_menu_item, this);
        init();
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_menu_item, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView);
        title = typedArray.getString(R.styleable.MenuItemView_title);
        subtitle=typedArray.getString(R.styleable.MenuItemView_subtitle);
        typedArray.recycle();
        init();
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.view_menu_item, this);
        init();
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(getContext(), R.layout.view_menu_item, this);
        init();
    }


}
