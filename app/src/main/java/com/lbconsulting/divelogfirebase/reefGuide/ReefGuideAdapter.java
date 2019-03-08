package com.lbconsulting.divelogfirebase.reefGuide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.ReefGuideItem;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ReefGuideAdapter extends RecyclerView.Adapter<ReefGuideAdapter.ViewHolder> {
    private int mReefGuideThumbnailWidthPx;
    private int mReefGuideThumbnailHeightPx;

    private final Context context;
    private final ArrayList<ReefGuideItem> reefGuideItems;
    private final boolean isSelectableReefGuide;

    public ReefGuideAdapter(Context context, ArrayList<ReefGuideItem> reefGuideItems,
                            boolean isSelectableReefGuide) {
        this.context = context;
        this.reefGuideItems = reefGuideItems;
        this.isSelectableReefGuide = isSelectableReefGuide;
        this.mReefGuideThumbnailHeightPx = (int) context.getResources()
                .getDimension(R.dimen.reef_guide_thumbnail_height);
        this.mReefGuideThumbnailWidthPx = (int) context.getResources()
                .getDimension(R.dimen.reef_guide_thumbnail_width);
    }

    @Override
    public ReefGuideAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_reef_guide_layout, viewGroup, false);
        return new ViewHolder(view,
                new ViewHolder.ReefGuideImageOnClick() {

                    @Override
                    public void onReefGuideImageClick(ImageAndCheckBoxViews caller) {

                        ImageView imageView = caller.getImageView();
                        CheckBox checkBox = caller.getCheckBox();
                        ReefGuideItem reefGuideItem = (ReefGuideItem) imageView.getTag();
                        if (reefGuideItem != null) {
                            if (checkBox.getVisibility() == View.VISIBLE) {
                                checkBox.setChecked(!checkBox.isChecked());
                                checkboxClicked(reefGuideItem, checkBox);
                            }

                        }
                    }
                },

                new ViewHolder.ReefGuideImageOnLongClick() {

                    @Override
                    public void onReefGuideImageLongClick(View caller) {
                        ReefGuideItem reefGuideItem = (ReefGuideItem) caller.getTag();
                        if (reefGuideItem != null && reefGuideItem.getReefGuideDetailUrl() != null
                                && !reefGuideItem.getReefGuideDetailUrl().equals(MySettings.NOT_AVAILABLE)) {
                            EventBus.getDefault().post(new MyEvents.showWebsiteDetail(reefGuideItem));

                        } else {
                            String title = "Unable to Show Website";
                            String msg = "Website Url not available";
                            MyMethods.showOkDialog(context, title, msg);
                        }
                    }
                },

                new ViewHolder.ReefGuideCheckboxOnClick() {

                    @Override
                    public void onReefGuideCheckboxClick(View caller) {
                        CheckBox checkBox = (CheckBox) caller;
                        ReefGuideItem reefGuideItem = (ReefGuideItem) checkBox.getTag();
                        if (reefGuideItem != null) {
                            checkboxClicked(reefGuideItem, checkBox);
                        }
                    }


                }
        );
    }

    private void checkboxClicked(ReefGuideItem reefGuideItem, CheckBox checkBox) {
        if (checkBox.isChecked()) {
            EventBus.getDefault().post(new MyEvents.addReefGuideItemToDiveLog(reefGuideItem));
        } else {
            EventBus.getDefault().post(new MyEvents.removeReefGuideItemFromDiveLog(reefGuideItem));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        String title = getTitle(reefGuideItems.get(i));
        String detail = getDetail(reefGuideItems.get(i));

        viewHolder.tv_title.setText(title);
        viewHolder.tv_detail.setText(detail);
        viewHolder.img_item.setTag(reefGuideItems.get(i));
        if (isSelectableReefGuide) {
            viewHolder.checkbox.setChecked(reefGuideItems.get(i).isChecked());
            viewHolder.checkbox.setTag(reefGuideItems.get(i));
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkbox.setVisibility(View.GONE);
        }
        Picasso.with(context).load(reefGuideItems.get(i).getThumbNailUrl())
                .resize(mReefGuideThumbnailWidthPx, mReefGuideThumbnailHeightPx)
                .placeholder(R.drawable.not_available)
                .into(viewHolder.img_item);
    }

    private String getTitle(ReefGuideItem reefGuideItem) {
        String title = reefGuideItem.getTitle();
        if (reefGuideItem.getAlsoKnownAs() != null) {
            title = title + "\n(" + reefGuideItem.getAlsoKnownAs() + ")";
        }
        return title;
    }

    private String getDetail(ReefGuideItem reefGuideItem) {
        String detail = reefGuideItem.getScientificName();

        if (reefGuideItem.getSize() != null) {
            if (detail == null) {
                detail = reefGuideItem.getSize();
            } else {
                detail = detail + "\n" + reefGuideItem.getSize();
            }
        }

        if (reefGuideItem.getDistribution() != null) {
            if (detail == null) {
                detail = reefGuideItem.getDistribution();
            } else {
                detail = detail + "\n" + reefGuideItem.getDistribution();
            }
        }
        return detail;
    }

    @Override
    public int getItemCount() {
        return reefGuideItems.size();
    }

    public void remove(int position) {
        ReefGuideItem reefGuideItem = reefGuideItems.get(position);
        EventBus.getDefault().post(new MyEvents.removeReefGuideItemFromDiveLog(reefGuideItem));
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private final TextView tv_title;
        private final TextView tv_detail;
        private final ImageView img_item;
        private final CheckBox checkbox;

        private final ReefGuideImageOnClick mReefGuideImageOnClick;
        private final ReefGuideImageOnLongClick mReefGuideImageOnLongClick;
        private final ReefGuideCheckboxOnClick mReefGuideCheckboxOnClick;

        ViewHolder(View view,
                   ReefGuideImageOnClick reefGuideImageOnClick,
                   ReefGuideImageOnLongClick reefGuideImageOnLongClick,
                   ReefGuideCheckboxOnClick reefGuideCheckboxOnClick) {
            super(view);

            mReefGuideImageOnClick = reefGuideImageOnClick;
            mReefGuideImageOnLongClick = reefGuideImageOnLongClick;
            mReefGuideCheckboxOnClick = reefGuideCheckboxOnClick;

            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_detail = (TextView) view.findViewById(R.id.tv_detail);
            img_item = (ImageView) view.findViewById(R.id.img_item);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);

            img_item.setOnClickListener(this);
            img_item.setOnLongClickListener(this);
            checkbox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_item:
                    ViewParent parentView = view.getParent();
                    CheckBox checkBox;
                    ImageView imageView = (ImageView) view;
                    if (parentView != null && parentView instanceof RelativeLayout) {
                        RelativeLayout r = (RelativeLayout) parentView;
                        checkBox = (CheckBox) r.findViewById(R.id.checkbox);
                        if (checkbox != null) {
                            ImageAndCheckBoxViews imageAndCheckBoxViews = new ImageAndCheckBoxViews(imageView, checkBox);
                            mReefGuideImageOnClick.onReefGuideImageClick(imageAndCheckBoxViews);
                        }
                    }

                    break;

                case R.id.checkbox:
                    mReefGuideCheckboxOnClick.onReefGuideCheckboxClick(view);
                    break;
            }
        }

        @Override
        public boolean onLongClick(View view) {

            switch (view.getId()) {
                case R.id.img_item:
                    mReefGuideImageOnLongClick.onReefGuideImageLongClick(view);
                    break;
            }
            return true;
        }

        interface ReefGuideImageOnClick {
            void onReefGuideImageClick(ImageAndCheckBoxViews caller);
        }

        interface ReefGuideImageOnLongClick {
            void onReefGuideImageLongClick(View caller);
        }

        interface ReefGuideCheckboxOnClick {
            void onReefGuideCheckboxClick(View caller);
        }
    }

    private static class ImageAndCheckBoxViews {
        private final ImageView img_item;
        private final CheckBox checkbox;

        ImageAndCheckBoxViews(ImageView img_item, CheckBox checkbox) {
            this.checkbox = checkbox;
            this.img_item = img_item;
        }

        CheckBox getCheckBox() {
            return checkbox;
        }

        public ImageView getImageView() {
            return img_item;
        }
    }
}

