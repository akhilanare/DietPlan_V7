package com.example.dietplan.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.Glide;
import com.example.dietplan.R;
import com.example.dietplan.activity.Login;
import com.example.dietplan.interfaces.FavouriteIF;
import com.example.dietplan.interfaces.OnClick;
import com.example.dietplan.item.VideoList;
import com.example.dietplan.util.Constant;
import com.example.dietplan.util.Method;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.textview.MaterialTextView;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class FitnessVideoAdapter extends RecyclerView.Adapter {

    private Method method;
    private Activity activity;
    private Animation myAnim;
    private String type;
    private int columnWidth;
    private List<VideoList> videoLists;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_Ad = 2;

    public FitnessVideoAdapter(Activity activity, String type, List<VideoList> videoLists, OnClick onClick) {
        this.activity = activity;
        this.videoLists = videoLists;
        this.type = type;
        method = new Method(activity, onClick);
        columnWidth = (method.getScreenWidth());
        myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.fitness_video_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType == VIEW_TYPE_Ad) {
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_ads, parent, false);
            return new AdOption(view);
        }
        return null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder instanceof ViewHolder) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            if (videoLists.get(position).getIs_fav().equals("true")) {
                viewHolder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
            } else {
                viewHolder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
            }

            viewHolder.imageView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, (int) (columnWidth / 2.2)));

            viewHolder.textView.setText(videoLists.get(position).getVideo_title());

            if (videoLists.get(position).getVideo_type().equals("youtube")) {
                Glide.with(activity).load(Constant.YOUTUBE_IMAGE_FRONT + videoLists.get(position).getVideo_id() + Constant.YOUTUBE_SMALL_IMAGE_BACK)
                        .placeholder(R.drawable.place_holder)
                        .into(viewHolder.imageView);
            } else {
                Glide.with(activity).load(videoLists.get(position).getVideo_thumbnail_s())
                        .placeholder(R.drawable.place_holder)
                        .into(viewHolder.imageView);
            }

            viewHolder.constraintLayout.setOnClickListener(v -> click(position));

            viewHolder.imageViewPlay.setOnClickListener(v -> click(position));

            viewHolder.imageViewFav.setOnClickListener(v -> {
                viewHolder.imageViewFav.startAnimation(myAnim);
                if (method.isLogin()) {
                    FavouriteIF favouriteIF = (isFavourite, message) -> {
                        if (isFavourite) {
                            videoLists.get(position).setIs_fav("true");
                            viewHolder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
                        } else {
                            videoLists.get(position).setIs_fav("false");
                            viewHolder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
                        }

                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    };
                    method.addToFav(videoLists.get(position).getId(), method.userId(), "video", 0, favouriteIF);
                } else {
                    Method.loginBack = true;
                    activity.startActivity(new Intent(activity, Login.class));
                }
            });

            viewHolder.imageViewShare.setOnClickListener(v -> {
                viewHolder.imageViewShare.startAnimation(myAnim);
                String string = activity.getResources().getString(R.string.Let_me_recommend_you_this_application) + "\n\n" + "https://play.google.com/store/apps/details?id=" + activity.getApplication().getPackageName();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, string);
                activity.startActivity(Intent.createChooser(intent, activity.getResources().getString(R.string.choose_one)));
            });

        } else if (holder.getItemViewType() == VIEW_TYPE_Ad) {
            AdOption adOption = (AdOption) holder;
            if (adOption.rl_native_ad.getChildCount() == 0 && Constant.appRP.isNative_ad() && !adOption.isAdRequested) {

                adOption.isAdRequested = true;

                switch (Constant.appRP.getNative_ad_type()) {
                    case Constant.AD_TYPE_ADMOB:
                    case Constant.AD_TYPE_FACEBOOK:

                        NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);

                        AdLoader adLoader = new AdLoader.Builder(activity, Constant.appRP.getNative_ad_id())
                                .forNativeAd(nativeAd -> {
                                    populateUnifiedNativeAdView(nativeAd, adView);
                                    adOption.rl_native_ad.removeAllViews();
                                    adOption.rl_native_ad.addView(adView);
                                    adOption.card_view.setVisibility(View.VISIBLE);
                                })
                                .build();

                        AdRequest.Builder builder = new AdRequest.Builder();
                        if (Method.personalization_ad) {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                        }
                        adLoader.loadAd(builder.build());

                        break;
                    case Constant.AD_TYPE_STARTAPP:
                        StartAppNativeAd nativeAd = new StartAppNativeAd(activity);

                        nativeAd.loadAd(new NativeAdPreferences()
                                .setAdsNumber(1)
                                .setAutoBitmapDownload(true)
                                .setPrimaryImageSize(2), new AdEventListener() {
                            @Override
                            public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                try {
                                    if (nativeAd.getNativeAds().size() > 0) {
                                        RelativeLayout nativeAdView = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_startapp, null);

                                        ImageView icon = nativeAdView.findViewById(R.id.icon);
                                        TextView title = nativeAdView.findViewById(R.id.title);
                                        TextView description = nativeAdView.findViewById(R.id.description);
                                        Button button = nativeAdView.findViewById(R.id.button);

                                        Glide.with(activity)
                                                .load(nativeAd.getNativeAds().get(0).getImageUrl())
                                                .into(icon);
                                        title.setText(nativeAd.getNativeAds().get(0).getTitle());
                                        description.setText(nativeAd.getNativeAds().get(0).getDescription());
                                        button.setText(nativeAd.getNativeAds().get(0).isApp() ? "Install" : "Open");

                                        adOption.rl_native_ad.removeAllViews();
                                        adOption.rl_native_ad.addView(nativeAdView);
                                        adOption.card_view.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailedToReceiveAd(Ad ad) {
                                adOption.isAdRequested = false;
                            }
                        });
                        break;
                    case Constant.AD_TYPE_APPLOVIN:
                        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Constant.appRP.getNative_ad_id(), activity);
                        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                            @Override
                            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                nativeAdView.setPadding(0, 0, 0, 10);
                                nativeAdView.setBackgroundColor(Color.WHITE);
                                adOption.rl_native_ad.removeAllViews();
                                adOption.rl_native_ad.addView(nativeAdView);
                                adOption.card_view.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                adOption.isAdRequested = false;
                            }

                            @Override
                            public void onNativeAdClicked(final MaxAd ad) {
                            }
                        });

                        nativeAdLoader.loadAd();
                        break;
                }
            }
        }

    }

    private void click(int position) {
        if (videoLists.get(position).getVideo_type().equals("youtube")) {
            method.click(position, type, "", videoLists.get(position).getVideo_title(), videoLists.get(position).getId(), videoLists.get(position).getVideo_type(), videoLists.get(position).getVideo_id());
        } else {
            method.click(position, type, "", videoLists.get(position).getVideo_title(), videoLists.get(position).getId(), videoLists.get(position).getVideo_type(), videoLists.get(position).getVideo_url());
        }
    }

    @Override
    public int getItemCount() {
        return videoLists.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (videoLists.size() == position) {
            return VIEW_TYPE_LOADING;
        } else if (videoLists.get(position) == null) {
            return VIEW_TYPE_Ad;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textView;
        private ConstraintLayout constraintLayout;
        private ImageView imageView, imageViewPlay, imageViewFav, imageViewShare;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_video_adapter);
            textView = itemView.findViewById(R.id.textView_video_adapter);
            imageViewFav = itemView.findViewById(R.id.imageView_fav_video_adapter);
            imageViewPlay = itemView.findViewById(R.id.imageView_play_video_adapter);
            imageViewShare = itemView.findViewById(R.id.imageView_share_video_adapter);
            constraintLayout = itemView.findViewById(R.id.con_video_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_loading);
        }
    }

    public class AdOption extends RecyclerView.ViewHolder {

        private CardView card_view;
        private final RelativeLayout rl_native_ad;
        private boolean isAdRequested;

        public AdOption(View itemView) {
            super(itemView);
            card_view = itemView.findViewById(R.id.card_view);
            rl_native_ad = itemView.findViewById(R.id.rl_native_ad);
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

}
