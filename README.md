# ChannelTagView
一个频道管理view，可拖拽排序，滑动删除。
----
<img src='https://github.com/yilylong/ChannelTagView/blob/master/GIF.gif'/>

使用
--

gradle 引用》  试用了gradle-bintray-plugin 和 bintray-release 插件都出现各种问题。有没有什么方法能传到maven 和jcenter?


xml中直接引用：

    <com.zhl.channeltagview.view.ChannelTagView
        android:id="@+id/channel_tag_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:fixedPos="0"
        app:channelItemTxSize="@dimen/channel_item_txsize"
        />
 
 
 调用 ChannelTagView的initChannels() 方法填充数据即可。有针对里面的item修改的各种属性，针对点击事件和用户的拖动滑动事件接口监听。详情查看demo
 
