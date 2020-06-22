package com.gary.olddermedicine.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.fragment.FragmentDrug;
import com.gary.olddermedicine.view.fragment.FragmentInfo;
import com.gary.olddermedicine.view.fragment.FragmentMain;
import com.gary.olddermedicine.view.fragment.FragmentNews;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.ycl.tabview.library.TabView;
import com.ycl.tabview.library.TabViewChild;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TabView tabView;
    private FlowingDrawer mDrawer;
    private ImageView mAvator,ic_back;
    private TextView infoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        tabView = findViewById(R.id.tabView);
        mDrawer = findViewById(R.id.drawerlayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mAvator = findViewById( R.id.avator );
        ic_back = findViewById( R.id.back_menu );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initTabView();
        initDrawer();
        mAvator.setOnClickListener( this);
        ic_back.setOnClickListener( this );
        infoName = findViewById(R.id.info_name);
        loadUser();
    }

    public void loadUser() {
        SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        String email = sp.getString("email", "XXX99999999");
        infoName.setText(email);
        for (int i = 26; i <= 26; i++) {
            fun(i);
        }
    }



    public static void fun(int day) {
        Random random = new Random();
        int sec = random.nextInt(60);
        Calendar instance = Calendar.getInstance();
        instance.set(2020, 3, day, 12, 35, sec);
        System.out.println("----------------------------");
        System.out.println("calen:" + instance.getTimeInMillis());
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        System.out.println(ft.format(new Date(instance.getTimeInMillis())));
        System.out.println("----------------------------");
    }

    private void initDrawer(){

        mDrawer.setTouchMode( ElasticDrawer.TOUCH_MODE_BEZEL);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED");
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });

    }

    private void initTabView(){
        final List<TabViewChild> tabViewChildList=new ArrayList<>();
        TabViewChild tabViewChildMain = new TabViewChild(R.drawable.tab_main,R.drawable.tab_main1,"首页",
                FragmentMain.newInstance("服药提醒"));
        final TabViewChild tabViewChildDrug = new TabViewChild(R.drawable.tab_drug,R.drawable.tab_drug1, "药物查询",
                FragmentDrug.newInstance("药物信息查询"));
        final TabViewChild tabViewChildNews = new TabViewChild(R.drawable.tab_news,R.drawable.tab_news1,"服药记录",
                FragmentNews.newInstance("查看服药记录"));
        TabViewChild tabViewChildInfo = new TabViewChild(R.drawable.tab_info,R.drawable.tab_info1,"我的",
                FragmentInfo.newInstance("我的"));
        tabViewChildList.add(tabViewChildMain);
        tabViewChildList.add(tabViewChildDrug);
        tabViewChildList.add(tabViewChildNews);
        tabViewChildList.add(tabViewChildInfo);
        //end add data
        tabView.setTabViewDefaultPosition(0);
        tabView.setTabViewChild(tabViewChildList,getSupportFragmentManager());
        tabView.setOnTabChildClickListener(new TabView.OnTabChildClickListener() {
            @Override
            public void onTabChildClick(int  position, ImageView currentImageIcon, TextView currentTextView) {
                if (tabViewChildList.get(position).equals(tabViewChildNews)) {
                    FragmentNews fragment = (FragmentNews)tabViewChildNews.getmFragment();
                    fragment.loadHistoryData();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.avator:
                mDrawer.openMenu();
                break;
            case R.id.back_menu:
                mDrawer.closeMenu();
                break;
            default:
                break;
        }
    }
}
