package com.trustbank.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trustbank.R;
import com.trustbank.util.AppConstants;
import com.trustbank.util.glide.GlideApp;

import java.util.ArrayList;

import pl.polidea.view.ZoomView;


public class SlidingImageAdapter extends PagerAdapter {

    private ArrayList<String> IMAGES;
    private LayoutInflater inflater;
    private Context context;
    private String Ip;

    public SlidingImageAdapter(Context context, ArrayList<String> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.sliding_image);

        imageView.setOnClickListener(view1 -> {
            displayImageDialog(AppConstants.IP + IMAGES.get(position));
        });

        GlideApp.with(context)
                .load(AppConstants.IP + IMAGES.get(position))
                .placeholder(R.drawable.dummy_daprt)
                .error(R.drawable.dummy_daprt)
                .into(imageView);

        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public void displayImageDialog(String imageUrl) {
        final Dialog dialogDisplayImage = new Dialog(context);
        dialogDisplayImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDisplayImage.setContentView(R.layout.dialog_dislpay_zoom_image);
        dialogDisplayImage.setCanceledOnTouchOutside(false);
        dialogDisplayImage.setCancelable(false);
        Window dialogWindow = dialogDisplayImage.getWindow();

        if (dialogWindow != null) {
            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            WindowManager.LayoutParams wlp = dialogWindow.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialogWindow.setAttributes(wlp);
        }
        dialogDisplayImage.show();
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_image_view, null, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));

        final ImageView imageViewDisplay = view.findViewById(R.id.imageView_Id);
        ImageButton imageButtonClose = dialogDisplayImage.findViewById(R.id.imageButtonClose_Id);
        RelativeLayout scrollViewMainContainer = dialogDisplayImage.findViewById(R.id.scrollViewMainContainer_Id);

        ZoomView zoomView = new ZoomView(context);
        zoomView.addView(view);
        scrollViewMainContainer.addView(zoomView);

        GlideApp.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.dummy_daprt)
                .error(R.drawable.dummy_daprt)
                .into(imageViewDisplay);

        imageButtonClose.setOnClickListener(view1 -> dialogDisplayImage.dismiss());
    }
}