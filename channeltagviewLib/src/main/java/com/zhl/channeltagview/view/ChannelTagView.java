package com.zhl.channeltagview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.zhl.channeltagview.R;
import com.zhl.channeltagview.adapter.GroupedGridLayoutManager;
import com.zhl.channeltagview.adapter.GroupedListAdapter;
import com.zhl.channeltagview.bean.ChannelItem;
import com.zhl.channeltagview.bean.GroupItem;
import com.zhl.channeltagview.listener.OnChannelItemClicklistener;
import com.zhl.channeltagview.listener.UserActionListener;
import com.zhl.channeltagview.util.MeasureUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeTextView;

/**
 * 描述：
 * Created by zhaohl on 2017-3-15.
 */

public class ChannelTagView extends LinearLayout {
    private static final int MIN_INTERVAL_TIME = 500;
    /**
     * 频道显示列数
     */
    public int colums = 5;
    /**
     * 频道列间距
     */
    public int columnHorizontalSpace = 10;
    /**
     * 频道行间距
     */
    public int columnVerticalSpace = 10;
    /**
     * 已添加的频道数据集合
     */
    private ArrayList<ChannelItem> addedChannels = new ArrayList<>();
    /**
     * 未添加的频道数据集合
     */
    private ArrayList<ChannelItem> unAddedChannels = new ArrayList<>();

    private ArrayList<GroupItem> unAddedGroups = new ArrayList<GroupItem>();

    private ArrayList<String> categoryList = new ArrayList<>();
    /**
     * 已、未添加的频道adapter
     */
    private ChannelAdapter addedAdapter;

    private GroupedListAdapter unAddedAdapter;
    /**
     * 处理recyclerview手势的辅助类
     */
    private ItemTouchHelper itemTouchHelper;
    /**
     * 频道点击事件回调接口
     */
    private OnChannelItemClicklistener onChannelItemClicklistener;
    /**
     * 用户操作已添加频道的一些手势事件回调接口
     */
    private UserActionListener userActionListener;
    /**
     * 红点提示view的处理回调接口
     */
    private RedDotRemainderListener redDotRemainderListener;
    /**
     * 是否显示添加后的轨迹动画
     */
    private boolean showPahtAnim;
    /**
     * 固定position
     */
    private int fixedPos = -1;
    /**
     * 固定频道的背景
     */
    private int fixedChannelBg;
    /**
     * 频道拖拽时的背景
     */
    private int channelItemDragingBg;
    /**
     * 频道item背景
     */
    private int channelItemBg;
    /**
     * 频道文字颜色
     */
    private int channelItemTxColor;
    /**
     * 频道文字大小
     */
    private int channelItemTxSize;
    /**
     * 栏目分组banner颜色
     */
    private int categoryAddedBannerBg, categoryUnAddedBannerBg;
    private RecyclerView addedRecyclerView, unaddedRecyclerView;
    /**
     * 栏目分组标题textview
     */
    private TextView categaryAddedTopView, categrayUnAddedTopView;
    private AnimatorSet pathAnimator;
    /**
     * 是否开启分组
     */
    private boolean openCategory;
    private GridLayoutManager addedLayoutManager;
    private GroupedGridLayoutManager unAddLayoutManager;
    private SpacesItemDecoration itemDecoration;
    /**
     * item 左侧icon drawable
     */
    private Drawable itemDrawableLeft;
    private int itemBgDrawableRes = -1;
    private boolean showItemDrawableLeft;
    private RelativeLayout.LayoutParams leftDrawableParams;
    /**
     * 是否可以侧滑
     */
    private boolean swipeEnable = true;
    private boolean longPressEnable = true;
    private long lastClickTime;


    public ChannelTagView(Context context) {
        this(context, null);
    }

    public ChannelTagView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ChannelTagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.channel_tag_style);
        showPahtAnim = array.getBoolean(R.styleable.channel_tag_style_showPathAnim, true);
        channelItemBg = array.getResourceId(R.styleable.channel_tag_style_channelItemBg, R.drawable.channel_item_bg);
        fixedChannelBg = array.getResourceId(R.styleable.channel_tag_style_channelItemFixedBg, R.drawable.fixed_item_bg);
        categoryAddedBannerBg = array.getResourceId(R.styleable.channel_tag_style_addedCategroyTitleBg, R.color.cmv_category_banner_view_bg);
        categoryUnAddedBannerBg = array.getResourceId(R.styleable.channel_tag_style_unAddedCategroyTitleBg, R.color.cmv_category_banner_view_bg);
        channelItemDragingBg = array.getResourceId(R.styleable.channel_tag_style_channelItemDragingBg, R.drawable.channel_item_draging);
        fixedPos = array.getInt(R.styleable.channel_tag_style_fixedPos, -1);
        colums = array.getInt(R.styleable.channel_tag_style_colums, 5);
        columnHorizontalSpace = array.getDimensionPixelOffset(R.styleable.channel_tag_style_columnHorizontalSpace, 10);
        columnVerticalSpace = array.getDimensionPixelOffset(R.styleable.channel_tag_style_columnVerticalSpace, 10);
        channelItemTxColor = array.getColor(R.styleable.channel_tag_style_channelItemTxColor, 0xff000000);
        channelItemTxSize = array.getDimensionPixelOffset(R.styleable.channel_tag_style_channelItemTxSize, 39);
        itemDrawableLeft = array.getDrawable(R.styleable.channel_tag_style_itemDrawableLeft);
        setOrientation(VERTICAL);
        init();
    }

    private void init() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.channel_tag_layout, this, true);
        addedRecyclerView = (RecyclerView) contentView.findViewById(R.id.added_channel_recyclerview);
        unaddedRecyclerView = (RecyclerView) contentView.findViewById(R.id.unAdded_channel_recyclerview);
        categaryAddedTopView = (TextView) contentView.findViewById(R.id.categray_added_title);
        categaryAddedTopView.setBackgroundResource(categoryAddedBannerBg);
        categrayUnAddedTopView = (TextView) contentView.findViewById(R.id.categray_unadded_title);
        categrayUnAddedTopView.setBackgroundResource(categoryUnAddedBannerBg);
        addedRecyclerView.setLayoutManager(addedLayoutManager = new GridLayoutManager(getContext(), colums));
        addedRecyclerView.addItemDecoration(itemDecoration = new SpacesItemDecoration(columnHorizontalSpace, columnVerticalSpace));
        unaddedRecyclerView.addItemDecoration(itemDecoration);
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public boolean isItemViewSwipeEnabled() {
                return swipeEnable;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return longPressEnable;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                int swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlag, swipeFlag);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                if (from == fixedPos) {
                    return false;
                }
                if (to == fixedPos) {
                    return false;
                }
                ChannelItem item = addedChannels.remove(from);
                addedChannels.add(to, item);
                addedAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                if (userActionListener != null) {
                    userActionListener.onMoved(fromPos, toPos, addedChannels);
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != fixedPos) {
                    ChannelItem removeChanle = addedChannels.remove(position);
                    addedAdapter.notifyItemRemoved(position);
                    insertToUnaddedChannel(removeChanle);
                    if (userActionListener != null) {
                        userActionListener.onSwiped(position, viewHolder.itemView, addedChannels, unAddedChannels);
                    }
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                int position = viewHolder.getAdapterPosition();
                if (position != fixedPos) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && position != fixedPos) {
                    float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    ViewCompat.setScaleX(viewHolder.itemView, 1.1f);
                    ViewCompat.setScaleY(viewHolder.itemView, 1.1f);
                    viewHolder.itemView.setBackgroundResource(channelItemDragingBg);
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                ViewCompat.setScaleX(viewHolder.itemView, 1.0f);
                ViewCompat.setScaleY(viewHolder.itemView, 1.0f);
                if (viewHolder.getAdapterPosition() == fixedPos) {
                    viewHolder.itemView.setBackgroundResource(fixedChannelBg);
                } else {
                    viewHolder.itemView.setBackgroundResource(channelItemBg);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(addedRecyclerView);
        addedRecyclerView.setAdapter(addedAdapter = new ChannelAdapter(getContext(), R.layout.item_channel_view, addedChannels));
        unaddedRecyclerView.setAdapter(unAddedAdapter = new GroupedListAdapter(getContext(), unAddedGroups, openCategory));
        unaddedRecyclerView.setLayoutManager(unAddLayoutManager = new GroupedGridLayoutManager(getContext(), colums, unAddedAdapter));
        addedAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (showItemDrawableLeft) {
                    return;
                }
                ((BGABadgeTextView) view.findViewById(R.id.item_tv)).hiddenBadge();
                if (onChannelItemClicklistener != null) {
                    onChannelItemClicklistener.onAddedChannelItemClick(view, position);
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        unAddedAdapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter groupedRecyclerViewAdapter, BaseViewHolder baseViewHolder, int groupPosition, int childPosition) {
                if (showItemDrawableLeft) {
                    return;
                }
                // 防止重复点击
                if ((System.currentTimeMillis() - lastClickTime) > MIN_INTERVAL_TIME) {
                    lastClickTime = System.currentTimeMillis();
                    int position = groupedRecyclerViewAdapter.getPositionForChild(groupPosition, childPosition);
                    View convertView = baseViewHolder.get(R.id.item_tv);
                    ((BGABadgeTextView) convertView).hiddenBadge();
                    ChannelItem removeItem = unAddedGroups.get(groupPosition).getChannelItems().remove(childPosition);
                    if (showPahtAnim) {
                        startPahtAnim(convertView, groupPosition, childPosition, removeItem);
                    } else {
                        unAddedAdapter.removeChild(groupPosition, childPosition);
                        addedChannels.add(removeItem);
                        addedAdapter.notifyItemInserted(addedChannels.size() - 1);
                    }
                    if (onChannelItemClicklistener != null) {
                        // 用户在这个回调处理自己的逻辑 如果分组 会添加一个分组头 所以要减去分组头
                        onChannelItemClicklistener.onUnAddedChannelItemClick(convertView, openCategory ? position - (groupPosition + 1) : position);
                    }
                }
            }
        });

    }

    private void insertToUnaddedChannel(ChannelItem removeChannel) {
        if (removeChannel.category == null || removeChannel.category.isEmpty()) {// 没有分组信息
            if (!categoryList.contains("其它")) {
                GroupItem groupItem = new GroupItem();
                groupItem.category = "其它";
                categoryList.add("其它");
                unAddedGroups.add(groupItem);
                unAddedAdapter.insertGroup(unAddedGroups.size() - 1);
            }
            unAddedGroups.get(unAddedGroups.size() - 1).addChanelItem(removeChannel);
            unAddedAdapter.insertChild(unAddedGroups.size() - 1, unAddedGroups.get(unAddedGroups.size() - 1).getChannelItems().size() - 1);
        } else {
            if (!categoryList.contains(removeChannel.category)) {
                GroupItem groupItem = new GroupItem();
                groupItem.category = removeChannel.category;
                categoryList.add(removeChannel.category);
                unAddedGroups.add(groupItem);
                unAddedAdapter.insertGroup(unAddedGroups.size() - 1);
            }
            for (int groupPos = 0; groupPos < unAddedGroups.size(); groupPos++) {
                GroupItem groupItem = unAddedGroups.get(groupPos);
                if (groupItem.category != null && groupItem.category.equals(removeChannel.category)) {
                    groupItem.addChanelItem(removeChannel);
                    unAddedAdapter.insertChild(groupPos, groupItem.getChannelItems().size() - 1);
                }
            }
        }
        getUnAddedChannels();
    }

    /**
     * 初始化频道数据
     *
     * @param addedChannels   已添加的频道
     * @param unAddedChannels 未添加的频道
     */
    public ChannelTagView initChannels(ArrayList<ChannelItem> addedChannels, ArrayList<GroupItem> unAddedChannels, boolean openCategory, RedDotRemainderListener redDotRemainderListener) {
        this.redDotRemainderListener = redDotRemainderListener;
        if (addedChannels != null) {
            this.addedChannels.clear();
            this.addedChannels.addAll(addedChannels);
            this.addedAdapter.notifyDataSetChanged();
        }
        if (unAddedChannels != null) {
            unAddedGroups.clear();
            unAddedGroups.addAll(unAddedChannels);
        }
        for (GroupItem group : unAddedGroups) {
            categoryList.add(group.category);
        }
        getUnAddedChannels();
        this.openCategory = openCategory;
        unAddedAdapter.setOpenCategory(openCategory);
        unAddedAdapter.setRedDotRemainderListener(redDotRemainderListener);
        return this;
    }

    /**
     * 是否开启分组
     *
     * @param openCategory
     */
    public void oPenCategory(boolean openCategory) {
        this.openCategory = openCategory;
        unAddedAdapter.setOpenCategory(openCategory);
    }

    /**
     * 当前是否已开启分组
     *
     * @return
     */
    public boolean isOpenCategory() {
        return openCategory;
    }

    /**
     * 开始轨迹动画
     *
     * @param tragetView
     * @param childposition
     */
    private void startPahtAnim(View tragetView, final int groupPosition, final int childposition, final ChannelItem removeItem) {
        if (pathAnimator != null && pathAnimator.isRunning()) {
            return;
        }
        View lastCheckedChild = addedRecyclerView.getChildAt(addedChannels.size() - 1);
        Rect rectLast = new Rect();
        int tragetX = 0;
        int tragetY = 0;
        int statusBarHeight = MeasureUtil.getStatusBarHeight((Activity) getContext());
        if (null != lastCheckedChild) {
            lastCheckedChild.getGlobalVisibleRect(rectLast);
            if (addedChannels.size() % colums == 0) {// 换行
                tragetX = addedRecyclerView.getLeft() + addedRecyclerView.getPaddingLeft() + columnHorizontalSpace;
                tragetY = rectLast.top + tragetView.getHeight() + 2 * columnVerticalSpace - statusBarHeight;
            } else {
                tragetX = rectLast.right + 2 * columnHorizontalSpace;
                tragetY = rectLast.top - statusBarHeight;
            }
        } else {
            rectLast.left = addedRecyclerView.getLeft() + addedRecyclerView.getPaddingLeft();
            rectLast.top = addedRecyclerView.getTop() + addedRecyclerView.getPaddingTop();
            tragetX = rectLast.left;
            tragetY = rectLast.top;
        }
        Rect rectTarget = new Rect();
        tragetView.getGlobalVisibleRect(rectTarget);
        final Point point = new Point();
        point.x = rectTarget.left;
        point.y = rectTarget.top - statusBarHeight;
        FloatItemViewManager.showFloatADwindow(getContext(), tragetView, removeItem.title, channelItemBg, point);

        final Point updatePoint = new Point();
        ValueAnimator animX = ValueAnimator.ofFloat(point.x, tragetX);
        animX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float valueX = (float) animation.getAnimatedValue();
                updatePoint.x = (int) valueX;
                FloatItemViewManager.updateFloatViewPosition(updatePoint);
            }
        });
        ValueAnimator animY = ValueAnimator.ofFloat(point.y, tragetY);
        animY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float valueY = (float) animation.getAnimatedValue();
                updatePoint.y = (int) valueY;
                FloatItemViewManager.updateFloatViewPosition(updatePoint);
            }
        });
//        final ChannelItem removeItem =  unAddedGroups.get(groupPosition).getChannelItems().remove(childposition);
        pathAnimator = new AnimatorSet();
        pathAnimator.setDuration(300);
        pathAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                unAddedAdapter.removeChild(groupPosition, childposition);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                addedChannels.add(removeItem);
                addedAdapter.notifyItemInserted(addedChannels.size() - 1);
                FloatItemViewManager.hideFloatView();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        pathAnimator.play(animX).with(animY);
        pathAnimator.start();

    }


    private class ChannelAdapter extends CommonAdapter<ChannelItem> {

        public ChannelAdapter(Context context, int layoutId, List<ChannelItem> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(final ViewHolder holder, ChannelItem channelItem, final int position) {
            final BGABadgeTextView title = (BGABadgeTextView) holder.getConvertView().findViewById(R.id.item_tv);
            final ImageView drawableLeft = (ImageView) holder.getConvertView().findViewById(R.id.item_drawable_left);
            if (fixedPos == position) {
                holder.getConvertView().setBackgroundResource(fixedChannelBg);
            } else {
                holder.getConvertView().setBackgroundResource(channelItemBg);
            }
            title.setTextColor(channelItemTxColor);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, channelItemTxSize);
            title.setText(channelItem.title);
            if (showItemDrawableLeft) {
                if (itemDrawableLeft != null) {
                    drawableLeft.setImageDrawable(itemDrawableLeft);
                }
                if (itemBgDrawableRes != -1) {
                    drawableLeft.setBackgroundResource(itemBgDrawableRes);
                }
                if (leftDrawableParams != null) {
                    drawableLeft.setLayoutParams(leftDrawableParams);
                }
                drawableLeft.setVisibility(VISIBLE);
                drawableLeft.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != onChannelItemClicklistener) {
                            int pos = holder.getAdapterPosition();
                            if (pos != fixedPos) {
                                ChannelItem removeChanle = addedChannels.remove(pos);
                                addedAdapter.notifyItemRemoved(pos);
                                insertToUnaddedChannel(removeChanle);
                                onChannelItemClicklistener.onItemDrawableClickListener(holder.getConvertView(), pos);
                            }
                        }
                    }
                });
            } else {
                drawableLeft.setVisibility(GONE);
            }
            if (redDotRemainderListener != null) {
                if (redDotRemainderListener.showAddedChannelBadge(title, position)) {
                    title.getBadgeViewHelper().setDragable(false);
                    redDotRemainderListener.handleAddedChannelReddot(title, position);
                } else {
                    title.hiddenBadge();
                }
            }
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int horizontalSpace, verticalSpace;

        public SpacesItemDecoration(int spaceH, int spaceV) {
            this.horizontalSpace = spaceH;
            this.verticalSpace = spaceV;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = horizontalSpace;
            outRect.right = horizontalSpace;
            outRect.bottom = verticalSpace;
            outRect.top = verticalSpace;
        }

        public int getHorizontalSpace() {
            return horizontalSpace;
        }

        public void setHorizontalSpace(int horizontalSpace) {
            this.horizontalSpace = horizontalSpace;
        }

        public int getVerticalSpace() {
            return verticalSpace;
        }

        public void setVerticalSpace(int verticalSpace) {
            this.verticalSpace = verticalSpace;
        }
    }

    /**
     * get Added channel group RecyclerView;
     *
     * @return
     */
    public RecyclerView getAddedRecyclerView() {
        return addedRecyclerView;
    }

    /**
     * get unAdded channel group RecyclerView
     *
     * @return
     */
    public RecyclerView getUnaddedRecyclerView() {
        return unaddedRecyclerView;
    }

    /**
     * 获取已添加栏目banner可以自定义文字内容和样式
     *
     * @return
     */
    public TextView getCategaryAddedTopView() {
        return categaryAddedTopView;
    }

    /**
     * 获取未添加栏目banner 可以自定义文字内容和样式
     *
     * @return
     */
    public TextView getCategrayUnAddedTopView() {
        return categrayUnAddedTopView;
    }

    /**
     * 是否开启轨迹动画
     *
     * @param showPahtAnim
     */
    public void showPahtAnim(boolean showPahtAnim) {
        this.showPahtAnim = showPahtAnim;
    }

    /**
     * 设置channel item的列数
     *
     * @param colums
     */
    public void setColums(int colums) {
        if (colums <= 0) {
            return;
        }
        this.colums = colums;
        addedLayoutManager.setSpanCount(colums);
        unAddLayoutManager.setSpanCount(colums);
    }


    /**
     * 获取列间距
     *
     * @return
     */
    public int getColumnHorizontalSpace() {
        return columnHorizontalSpace;
    }

    /**
     * 设置列间距
     *
     * @param columnHorizontalSpace
     */
    public void setColumnHorizontalSpace(int columnHorizontalSpace) {
        if (columnHorizontalSpace < 0) {
            return;
        }
        this.columnHorizontalSpace = columnHorizontalSpace;
        itemDecoration.setHorizontalSpace(columnHorizontalSpace);
    }

    /**
     * 获取行间距
     *
     * @return
     */
    public int getColumnVerticalSpace() {
        return columnVerticalSpace;
    }

    /**
     * 获取行间距
     *
     * @param columnVerticalSpace
     */
    public void setColumnVerticalSpace(int columnVerticalSpace) {
        if (columnVerticalSpace < 0) {
            return;
        }
        this.columnVerticalSpace = columnVerticalSpace;
        itemDecoration.setVerticalSpace(columnVerticalSpace);
    }

    /**
     * set which  channel item fixed position
     *
     * @param fixedPos
     */
    public void setFixedPos(int fixedPos) {
        this.fixedPos = fixedPos;
    }

    /**
     * remove fixed position
     */
    public void removeFixedItem() {
        this.fixedPos = -1;
        addedAdapter.notifyDataSetChanged();
    }

    /**
     * set fixed item background ResID
     *
     * @param fixedChannelBgResID
     */
    public void setFixedChannelBg(int fixedChannelBgResID) {
        this.fixedChannelBg = fixedChannelBgResID;
    }

    /**
     * set item draging background Res ID
     *
     * @param channelItemDragingBgResID
     */
    public void setChannelItemDragingBg(int channelItemDragingBgResID) {
        this.channelItemDragingBg = channelItemDragingBgResID;
    }

    /**
     * set channel item background ResID
     *
     * @param channelItemBgResID
     */
    public void setChannelItemBg(int channelItemBgResID) {
        this.channelItemBg = channelItemBgResID;
        unAddedAdapter.setItemBg(channelItemBg);
    }

    /**
     * 设置频道字体颜色
     *
     * @param channelItemTxColor 颜色值
     */
    public void setChannelItemTxColor(int channelItemTxColor) {
        this.channelItemTxColor = channelItemTxColor;
        unAddedAdapter.setItemTxColor(channelItemTxColor);
    }

    /**
     * 设置频道字体大小
     *
     * @param pixel 大小（pixel）
     */
    public void setChannelItemTxSizePixel(int pixel) {
        this.channelItemTxSize = channelItemTxSize;
    }

    /**
     * 设置频道字体大小(sp)
     *
     * @param sp 大小（sp）
     */
    public void setChannelItemTxSizeSP(int sp) {
        this.channelItemTxSize = MeasureUtil.sp2px(getContext(), channelItemTxSize);
    }

    /**
     * 设置已添加栏目的banner background
     *
     * @param categoryAddedBannerBg
     */
    public void setCategoryAddedBannerBg(int categoryAddedBannerBg) {
        this.categoryAddedBannerBg = categoryAddedBannerBg;
        categaryAddedTopView.setBackgroundResource(categoryAddedBannerBg);
    }

    /**
     * 设置未添加栏目的banner background
     *
     * @param categoryUnAddedBannerBg
     */
    public void setCategoryUnAddedBannerBg(int categoryUnAddedBannerBg) {
        this.categoryUnAddedBannerBg = categoryUnAddedBannerBg;
        categrayUnAddedTopView.setBackgroundResource(categoryUnAddedBannerBg);
    }

    /**
     * 设置已添加栏目banner的文字
     *
     * @param bannerTX
     */
    public void setCategaryAddedBannerTX(String bannerTX) {
        categaryAddedTopView.setText(bannerTX);
    }

    /**
     * 设置未添加栏目的banner文字
     *
     * @param bannerTX
     */
    public void setCategrayUnAddedBannerTX(String bannerTX) {
        categrayUnAddedTopView.setText(bannerTX);
    }

    /**
     * 设置 banner的文字大小
     *
     * @param pixel
     */
    public void setCategoryBannerTXsize(int pixel) {
        categaryAddedTopView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixel);
        categrayUnAddedTopView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixel);
    }

    /**
     * 设置banner的文字颜色
     *
     * @param colorValue
     */
    public void setCategoryBannerTXColor(int colorValue) {
        categaryAddedTopView.setTextColor(colorValue);
        categrayUnAddedTopView.setTextColor(colorValue);
    }

    /**
     * 设置未添加栏目的分组头 bg
     *
     * @param bgResID
     */
    public void setCategoryItemBg(int bgResID) {
        unAddedAdapter.setCategoryBg(bgResID);
    }

    /**
     * 设置未添加栏目的分组头文字颜色
     *
     * @param color
     */
    public void setCategoryItemTxColor(int color) {
        unAddedAdapter.setCategoryTxColor(color);
    }

    /**
     * 设置未添加栏目的分组头文字大小
     *
     * @param size
     */
    public void setCategoryItemTxSize(int size) {
        unAddedAdapter.setCategoryTxSize(size);
    }

    /**
     * get weather swipe enable
     *
     * @return true or false
     */
    public boolean isSwipeEnable() {
        return swipeEnable;
    }

    /**
     * set weather swipe enable
     */
    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FloatItemViewManager.removeFloawAdView(getContext());
    }

    /**
     * weather or not show item left drawable
     *
     * @param showDrawableLeft
     */
    public void showItemDrawableLeft(boolean showDrawableLeft) {
        showItemDrawableLeft = showDrawableLeft;
//        longPressEnable = !showItemDrawableLeft;
//        swipeEnable = !showDrawableLeft;
        addedAdapter.notifyDataSetChanged();
    }

    /**
     * get weather or not show item left drawbale
     *
     * @return
     */
    public boolean isShowItemDrawableLeft() {
        return showItemDrawableLeft;
    }

    /**
     * set item left drawable
     *
     * @param itemDrawableLeft
     */
    public void setItemDrawableLeft(Drawable itemDrawableLeft) {
        this.itemDrawableLeft = itemDrawableLeft;
    }

    public void setItemLeftDrawableBgRes(int itemLeftDrawableBgRes) {
        this.itemBgDrawableRes = itemLeftDrawableBgRes;
    }

    public void setItemLeftDrawableLayoutParams(RelativeLayout.LayoutParams layoutParams) {
        this.leftDrawableParams = layoutParams;
    }

    ;

    public ArrayList<ChannelItem> getUnAddedChannels() {
        unAddedChannels.clear();
        for (GroupItem groupItem : unAddedGroups) {
            unAddedChannels.addAll(groupItem.getChannelItems());
        }
        return unAddedChannels;
    }

    public ArrayList<ChannelItem> getAddedChannels() {
        return addedChannels;
    }

    /**
     * 描述：频道item的红点提示处理回调接口
     * author: zhl 2017-3-20
     */
    public interface RedDotRemainderListener {
        /**
         * 已添加栏目是否显示红点提示view
         *
         * @param itemView
         * @param position
         * @return return true show the bgabadgeTipView otherwish not show
         */
        public boolean showAddedChannelBadge(BGABadgeTextView itemView, int position);

        /**
         * 未添加栏目是否显示红点提示view
         *
         * @param itemView
         * @param position
         * @return return true show the bgabadgeTipView otherwish not show
         */
        public boolean showUnAddedChannelBadge(BGABadgeTextView itemView, int position);

        /**
         * 处理已添加栏目红点提示view 通过BGABadgeTextView.getBadgeViewHelper()可以设置红点提示view一些属性 如：间距 显示样式 文字大小等等
         *
         * @param itemView
         * @param position
         */
        public void handleAddedChannelReddot(BGABadgeTextView itemView, int position);

        /**
         * 处理已添加栏目红点提示view 通过BGABadgeTextView.getBadgeViewHelper()可以设置红点提示view一些属性 如：间距 显示样式 文字大小等等
         *
         * @param itemView
         * @param position
         */
        public void handleUnAddedChannelReddot(BGABadgeTextView itemView, int position);

        /**
         * 拖拽提示view消失回调
         *
         * @param itemView
         * @param position
         */
        public void OnDragDismiss(BGABadgeTextView itemView, int position);

    }

    /**
     * 设置channel item 的点击回调事件
     *
     * @param onChannelItemClicklistener
     */
    public void setOnChannelItemClicklistener(OnChannelItemClicklistener onChannelItemClicklistener) {
        this.onChannelItemClicklistener = onChannelItemClicklistener;
    }

    /**
     * 设置对channel item的拖拽、欢动的操作回调监听
     *
     * @param userActionListener
     */
    public void setUserActionListener(UserActionListener userActionListener) {
        this.userActionListener = userActionListener;
    }
}
