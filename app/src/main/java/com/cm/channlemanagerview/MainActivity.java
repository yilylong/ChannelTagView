package com.cm.channlemanagerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.zhl.channeltagview.bean.ChannelItem;
import com.zhl.channeltagview.listener.OnChannelItemClicklistener;
import com.zhl.channeltagview.listener.UserActionListener;
import com.zhl.channeltagview.view.ChannelTagView;

import java.util.ArrayList;

import cn.bingoogolapple.badgeview.BGABadgeTextView;

public class MainActivity extends AppCompatActivity {
    private ChannelTagView channelTagView;
    private ArrayList<ChannelItem> addedChannels = new ArrayList<>();
    private ArrayList<ChannelItem> unAddedChannels = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        channelTagView = (ChannelTagView) findViewById(R.id.channel_tag_view);
        initData();
        channelTagView.initChannels(addedChannels, unAddedChannels, new ChannelTagView.RedDotRemainderListener() {

            @Override
            public boolean showAddedChannelBadge(BGABadgeTextView itemView, int position) {
                if(addedChannels.get(position).title.equals("生活")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public boolean showUnAddedChannelBadge(BGABadgeTextView itemView, int position) {
                if(unAddedChannels.get(position).title.equals("数码")||unAddedChannels.get(position).title.equals("科技")){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void handleAddedChannelReddot(BGABadgeTextView itemView, int position) {
                itemView.showCirclePointBadge();
            }

            @Override
            public void handleUnAddedChannelReddot(BGABadgeTextView itemView, int position) {
                if(unAddedChannels.get(position).title.equals("科技")){
                    itemView.showTextBadge("new");
                }else{
                    itemView.showCirclePointBadge();
                }
            }

            @Override
            public void OnDragDismiss(BGABadgeTextView itemView, int position) {

            }

        });
//        channelTagView.setFixedChannelBg(R.drawable.fixed_item_bg);
//        channelTagView.setFixedChannel(0);
//        channelTagView.showPahtAnim(true);
        channelTagView.setOnChannelItemClicklistener(new OnChannelItemClicklistener() {

            @Override
            public void onAddedChannelItemClick(View itemView, int position) {
                Toast.makeText(MainActivity.this,"打开-"+addedChannels.get(position).title,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnAddedChannelItemClick(View itemView, int position) {
                ChannelItem item = unAddedChannels.remove(position);
                addedChannels.add(item);
                Toast.makeText(MainActivity.this,"添加频道-"+item.title,Toast.LENGTH_SHORT).show();
            }
        });
        channelTagView.setUserActionListener(new UserActionListener() {
            @Override
            public void onMoved(int fromPos, int toPos, ArrayList<ChannelItem> checkedChannels) {
                Toast.makeText(MainActivity.this,"将-"+addedChannels.get(fromPos).title+" 换到 "+addedChannels.get(toPos).title,Toast.LENGTH_SHORT).show();
                addedChannels.clear();
                addedChannels.addAll(checkedChannels);
            }

            @Override
            public void onSwiped(int position, View itemView, ArrayList<ChannelItem> checkedChannels, ArrayList<ChannelItem> uncheckedChannels) {
                Toast.makeText(MainActivity.this,"删除-"+MainActivity.this.addedChannels.remove(position).title,Toast.LENGTH_SHORT).show();
                unAddedChannels.clear();
                unAddedChannels.addAll(uncheckedChannels);
            }
        });
    }

    private void initData() {
        String[] chanles = getResources().getStringArray(R.array.chanles);
        for (int i = 0; i < chanles.length/2; i++) {
            ChannelItem item = new ChannelItem();
            item.id = i;
            item.title = chanles[i];
            addedChannels.add(item);
        }
        for (int i = chanles.length/2; i < chanles.length; i++) {
            ChannelItem item = new ChannelItem();
            item.id = i;
            item.title = chanles[i];
            unAddedChannels.add(item);
        }
    }
}
