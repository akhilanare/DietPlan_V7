package com.example.dietplan.rest;

import com.example.dietplan.response.AboutUsRP;
import com.example.dietplan.response.AppRP;
import com.example.dietplan.response.BmiHistoryRP;
import com.example.dietplan.response.CategoryRP;
import com.example.dietplan.response.ContactRP;
import com.example.dietplan.response.DataRP;
import com.example.dietplan.response.DietPlanDetailRP;
import com.example.dietplan.response.DietPlanRP;
import com.example.dietplan.response.FaqRP;
import com.example.dietplan.response.FavouriteRP;
import com.example.dietplan.response.FitnessVideoRP;
import com.example.dietplan.response.HomeRP;
import com.example.dietplan.response.LoginRP;
import com.example.dietplan.response.PrivacyPolicyRP;
import com.example.dietplan.response.ProfileRP;
import com.example.dietplan.response.RegisterRP;
import com.example.dietplan.response.TermsConditionsRP;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    //get app data
    @POST("api.php")
    @FormUrlEncoded
    Call<AppRP> getAppData(@Field("data") String data);

    //get about us
    @POST("api.php")
    @FormUrlEncoded
    Call<AboutUsRP> getAboutUs(@Field("data") String data);

    //get privacy policy
    @POST("api.php")
    @FormUrlEncoded
    Call<PrivacyPolicyRP> getPrivacyPolicy(@Field("data") String data);

    //get terms condition
    @POST("api.php")
    @FormUrlEncoded
    Call<TermsConditionsRP> getTermsCondition(@Field("data") String data);

    //get faq
    @POST("api.php")
    @FormUrlEncoded
    Call<FaqRP> getFaq(@Field("data") String data);

    //get contact subject
    @POST("api.php")
    @FormUrlEncoded
    Call<ContactRP> getContactSub(@Field("data") String data);

    //submit contact
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> submitContact(@Field("data") String data);

    //login
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLogin(@Field("data") String data);

    //register
    @POST("api.php")
    @FormUrlEncoded
    Call<RegisterRP> getRegister(@Field("data") String data);

    //forgot password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> getForgotPass(@Field("data") String data);

    //login check
    @POST("api.php")
    @FormUrlEncoded
    Call<LoginRP> getLoginDetail(@Field("data") String data);

    //get profile detail
    @POST("api.php")
    @FormUrlEncoded
    Call<ProfileRP> getProfile(@Field("data") String data);

    //edit profile
    @POST("api.php")
    @Multipart
    Call<DataRP> editProfile(@Part("data") RequestBody data, @Part MultipartBody.Part part);

    //update password
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> updatePassword(@Field("data") String data);

    //get home data
    @POST("api.php")
    @FormUrlEncoded
    Call<HomeRP> getHome(@Field("data") String data);

    //category
    @POST("api.php")
    @FormUrlEncoded
    Call<CategoryRP> getCategory(@Field("data") String data);

    //get diet plan list
    @POST("api.php")
    @FormUrlEncoded
    Call<DietPlanRP> getDietPlanList(@Field("data") String data);

    //get single diet plan detail
    @POST("api.php")
    @FormUrlEncoded
    Call<DietPlanDetailRP> getDietPlanDetail(@Field("data") String data);

    //fitness video
    @POST("api.php")
    @FormUrlEncoded
    Call<FitnessVideoRP> getFitnessVideo(@Field("data") String data);

    //favourite
    @POST("api.php")
    @FormUrlEncoded
    Call<FavouriteRP> isFavouriteOrNot(@Field("data") String data);

    //submit bmi history
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> submitBmiHistory(@Field("data") String data);

    //get bmi history
    @POST("api.php")
    @FormUrlEncoded
    Call<BmiHistoryRP> getBmiHistory(@Field("data") String data);

    //delete bmi history
    @POST("api.php")
    @FormUrlEncoded
    Call<DataRP> deleteBmiHistory(@Field("data") String data);

}
