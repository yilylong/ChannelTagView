package com.cm.channlemanagerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhl.channeltagview.bean.ChannelItem;
import com.zhl.channeltagview.bean.GroupItem;
import com.zhl.channeltagview.listener.OnChannelItemClicklistener;
import com.zhl.channeltagview.listener.UserActionListener;
import com.zhl.channeltagview.view.ChannelTagView;

import java.util.ArrayList;

import cn.bingoogolapple.badgeview.BGABadgeTextView;

public class MainActivity extends AppCompatActivity {
    private ChannelTagView channelTagView;
    private ArrayList<ChannelItem> addedChannels = new ArrayList<>();
    private ArrayList<ChannelItem> unAddedChannels = new ArrayList<>();
    private ArrayList<GroupItem> unAddedItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        channelTagView = (ChannelTagView) findViewById(R.id.channel_tag_view);
        initData();
        Button btn = (Button) findViewById(R.id.btn_opencategory);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channelTagView.oPenCategory(!channelTagView.isOpenCategory());
            }
        });
        channelTagView.showPahtAnim(true);
        channelTagView.setCategoryItemBg(R.color.content_color);
//        channelTagView.setChannelItemTxColor(Color.BLUE);
//        channelTagView.setChannelItemBg(R.drawable.custom_channel_item_bg);
//        channelTagView.setCategrayUnAddedBannerTX("更多栏目");
//        channelTagView.setCategoryAddedBannerBg(R.color.content_color);
//        channelTagView.setCategoryBannerTXsize(40);
//        channelTagView.setCategoryBannerTXColor(Color.argb(255,221,224,98));
//        channelTagView.setColumnVerticalSpace(20);
        channelTagView.initChannels(addedChannels, unAddedItems,true,new ChannelTagView.RedDotRemainderListener() {

            @Override
            public boolean showAddedChannelBadge(BGABadgeTextView itemView, int position) {
                if(addedChannels.get(position).title.equals("直播")){
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
                Toast.makeText(MainActivity.this,"拖拽取消红点提示-",Toast.LENGTH_SHORT).show();
                itemView.hiddenBadge();
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
        for (int i = 0; i < 7; i++) {
            ChannelItem item = new ChannelItem();
            item.id = i;
            item.title = chanles[i];
            item.category ="头条";
            addedChannels.add(item);
        }
        GroupItem groupFinance = new GroupItem();
        groupFinance.category = "金融";
        for (int i = 7; i < 9; i++) {
            ChannelItem item = new ChannelItem();
            item.id = i;
            item.title = chanles[i];
            item.category ="金融";
            unAddedChannels.add(item);
            groupFinance.addChanelItem(item);
        }
        unAddedItems.add(groupFinance);

        GroupItem groupLife = new GroupItem();
        groupLife.category = "生活";
        for (int i = 9; i < 18; i++) {
            ChannelItem item = new ChannelItem();
            item.id = i;
            item.title = chanles[i];
            item.category ="生活";
            unAddedChannels.add(item);
            groupLife.addChanelItem(item);
        }
        unAddedItems.add(groupLife);

        GroupItem groupEntertainment = new GroupItem();
        groupEntertainment.category = "娱乐";
        for (int i = 18; i < 22; i++) {
            ChannelItem item = new ChannelItem();
            item.id = i;
            item.title = chanles[i];
            item.category ="娱乐";
            unAddedChannels.add(item);
            groupEntertainment.addChanelItem(item);
        }
        unAddedItems.add(groupEntertainment);

        GroupItem Grouphumanity = new GroupItem();
        Grouphumanity.category = "人文";
        for (int i = 22; i <= 25; i++) {
            ChannelItem item = new ChannelItem();
            item.id = i;
            item.title = chanles[i];
            item.category ="人文";
            unAddedChannels.add(item);
            Grouphumanity.addChanelItem(item);
        }
        unAddedItems.add(Grouphumanity);
    }
}
