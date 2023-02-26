package com.example.dietplan.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.dietplan.R;
import com.example.dietplan.activity.MainActivity;
import com.example.dietplan.response.DataRP;
import com.example.dietplan.rest.ApiClient;
import com.example.dietplan.rest.ApiInterface;
import com.example.dietplan.util.API;
import com.example.dietplan.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kevalpatel2106.rulerpicker.RulerValuePicker;
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BmiCalculatorFragment extends Fragment {

    private Method method;
    private boolean isMale = true;
    private Animation myAnim;
    private ProgressDialog progressDialog;
    private float height = 0, weight = 0;
    private ConstraintLayout conMale, conFemale;
    private ImageView imageViewMale, imageViewFemale;
    private MaterialTextView textViewMale, textViewFemale;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bmi_calculator_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.bmi_calculator));
        }

        progressDialog = new ProgressDialog(getActivity());

        method = new Method(getActivity());
        method.forceRTLIfSupported();

        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        RulerValuePicker rulerValuePickerHeight = view.findViewById(R.id.ruler_picker_height_bmi);
        RulerValuePicker rulerValuePickerWeight = view.findViewById(R.id.ruler_picker_weight_bmi);

        rulerValuePickerHeight.selectValue(140);//set selected value height
        rulerValuePickerWeight.selectValue(40);//set selected value weight

        textViewMale = view.findViewById(R.id.textView_male_bmi);
        textViewFemale = view.findViewById(R.id.textView_female_bmi);
        imageViewMale = view.findViewById(R.id.imageView_male_bmi);
        imageViewFemale = view.findViewById(R.id.imageView_female_bmi);
        MaterialButton buttonBmi = view.findViewById(R.id.button_bmi);
        conMale = view.findViewById(R.id.con_male_bmi);
        conFemale = view.findViewById(R.id.con_female_bmi);

        isGender();

        rulerValuePickerHeight.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(final int selectedValue) {
                height = selectedValue;
                //Value changed and the user stopped scrolling the ruler.
                //Application can consider this value as final selected value.
            }

            @Override
            public void onIntermediateValueChange(final int selectedValue) {
                //Value changed but the user is still scrolling the ruler.
                //This value is not final value. Application can utilize this value to display the current selected value.
            }
        });

        rulerValuePickerWeight.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(final int selectedValue) {
                weight = selectedValue;
                //Value changed and the user stopped scrolling the ruler.
                //Application can consider this value as final selected value.
            }

            @Override
            public void onIntermediateValueChange(final int selectedValue) {
                //Value changed but the user is still scrolling the ruler.
                //This value is not final value. Application can utilize this value to display the current selected value.
            }
        });

        conMale.setOnClickListener(v -> {
            conMale.startAnimation(myAnim);
            isMale = true;
            isGender();
        });

        conFemale.setOnClickListener(v -> {
            conFemale.startAnimation(myAnim);
            isMale = false;
            isGender();
        });

        buttonBmi.setOnClickListener(v -> {

            if (height <= 0) {
                method.alertBox(getResources().getString(R.string.please_select_height));
            } else if (weight <= 0) {
                method.alertBox(getResources().getString(R.string.please_select_weight));
            } else {
                submitBmiHistory(method.userId(), isMale);
            }

        });

        return view;

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void isGender() {
        if (isMale) {
            textViewMale.setTextColor(getResources().getColor(R.color.white));
            imageViewMale.setImageDrawable(getResources().getDrawable(R.drawable.male_se));
            textViewFemale.setTextColor(getResources().getColor(R.color.textView_bmi));
            if (method.isDarkMode()) {
                imageViewFemale.setImageDrawable(getResources().getDrawable(R.drawable.dark_female_un));
            } else {
                imageViewFemale.setImageDrawable(getResources().getDrawable(R.drawable.female_un));
            }
            conMale.setBackground(getResources().getDrawable(R.drawable.select_gender_bg));
            conFemale.setBackground(getResources().getDrawable(R.drawable.un_select_gender_bg));
        } else {
            textViewMale.setTextColor(getResources().getColor(R.color.textView_bmi));
            if (method.isDarkMode()) {
                imageViewMale.setImageDrawable(getResources().getDrawable(R.drawable.dark_male_un));
            } else {
                imageViewMale.setImageDrawable(getResources().getDrawable(R.drawable.male_un));
            }
            textViewFemale.setTextColor(getResources().getColor(R.color.white));
            imageViewFemale.setImageDrawable(getResources().getDrawable(R.drawable.female_se));
            conMale.setBackground(getResources().getDrawable(R.drawable.un_select_gender_bg));
            conFemale.setBackground(getResources().getDrawable(R.drawable.select_gender_bg));
        }
    }

    private void submitBmiHistory(String userId, boolean isGender) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            //formula bmi calculation
            float heightValue = height / 100;
            float weightValue = weight;
            final float bmi = weightValue / (heightValue * heightValue);

            String gender;
            if (isGender) {
                gender = "MALE";
            } else {
                gender = "FEMALE";
            }

            if (method.isNetworkAvailable()) {

                JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
                jsObj.addProperty("user_id", userId);
                jsObj.addProperty("bmi_score", String.valueOf(bmi));
                jsObj.addProperty("bmi_status", displayBMI(bmi));
                jsObj.addProperty("gender", gender);
                jsObj.addProperty("method_name", "add_bmi");
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<DataRP> call = apiService.submitBmiHistory(API.toBase64(jsObj.toString()));
                call.enqueue(new Callback<DataRP>() {
                    @Override
                    public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                        if (getActivity() != null) {
                            try {
                                DataRP dataRP = response.body();
                                assert dataRP != null;

                                if (dataRP.getStatus().equals("1")) {

                                    if (dataRP.getSuccess().equals("1")) {
                                        showBmiDialog(bmi);
                                    } else {
                                        alertBox(getResources().getString(R.string.bmi_submit_fail_message), bmi);
                                    }

                                } else if (dataRP.getStatus().equals("2")) {
                                    method.suspend(dataRP.getMessage());
                                } else {
                                    method.alertBox(dataRP.getMessage());
                                }

                            } catch (Exception e) {
                                Log.d("exception_error", e.toString());
                                alertBox(getResources().getString(R.string.bmi_submit_fail_message), bmi);
                            }
                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                        // Log error here since request failed
                        Log.e("fail", t.toString());
                        progressDialog.dismiss();
                        alertBox(getResources().getString(R.string.bmi_submit_fail_message), bmi);
                    }
                });

            } else {
                progressDialog.dismiss();
                alertBox(getResources().getString(R.string.bmi_submit_fail_message), bmi);
            }

        }

    }

    //get bmi result
    private String displayBMI(float bmi) {

        if (isMale) {
            if (bmi < 18.5) {
                return getResources().getString(R.string.Underweight);
            } else if (bmi < 25) {
                return getResources().getString(R.string.Normal);
            } else if (bmi < 30) {
                return getResources().getString(R.string.Overweight);
            } else {
                return getResources().getString(R.string.Obese);
            }
        } else {
            if (bmi < 16.5) {
                return getResources().getString(R.string.Underweight);
            } else if (bmi < 22) {
                return getResources().getString(R.string.Normal);
            } else if (bmi < 27) {
                return getResources().getString(R.string.Overweight);
            } else {
                return getResources().getString(R.string.Obese);
            }
        }

    }

    private void showBmiDialog(float bmi) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_bmi_result);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(MaterialCardView.LayoutParams.MATCH_PARENT, MaterialCardView.LayoutParams.WRAP_CONTENT);

        MaterialTextView textViewScore = dialog.findViewById(R.id.textView_scoreBmi_dialog_bmi);
        MaterialTextView textViewStatus = dialog.findViewById(R.id.textView_bmiStatus_dialog_bmi);
        ImageView imageViewClose = dialog.findViewById(R.id.imageView_close_dialog_bmi);
        MaterialButton buttonTryAgain = dialog.findViewById(R.id.button_tryAgain_dialog);
        MaterialButton buttonShare = dialog.findViewById(R.id.button_share_dialog);

        buttonTryAgain.setOnClickListener(v1 -> dialog.dismiss());

        buttonShare.setOnClickListener(v2 -> shareBmi(String.valueOf(bmi), displayBMI(bmi)));

        textViewScore.setText(String.valueOf(bmi));
        textViewStatus.setText(displayBMI(bmi));

        imageViewClose.setOnClickListener(v3 -> dialog.dismiss());

        dialog.show();
    }

    //alert message box
    public void alertBox(String message, float bmi) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.DialogTitleTextStyle);
        builder.setMessage(Html.fromHtml(message));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.ok),
                (arg0, arg1) -> {
                    showBmiDialog(bmi);
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void shareBmi(String score, String result) {

        String string = getResources().getString(R.string.your_bmi_score) + " " + "=" + " " + score + "\n" + getResources().getString(R.string.status) + " " + "=" + " " + result
                + "\n" + "\n" + "https://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
        shareIntent.setType("text/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, string);
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.choose_one)));

    }

}
