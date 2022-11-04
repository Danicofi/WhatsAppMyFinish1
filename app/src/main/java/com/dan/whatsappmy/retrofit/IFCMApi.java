package com.dan.whatsappmy.retrofit;

import com.dan.whatsappmy.models.FCMBody;
import com.dan.whatsappmy.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAfoxJBCM:APA91bFI499gOVDkxWri7pKoHBMM02ng2m_l10nolG2HBHWU5pfY-rn-eu91uM-Z2wQwlryml1h_u_0pQkcVOmvTf1TS-Vp44EveaPpNmo0yf_7Xp8eZDKgFqZQSTtcZumb4u_XGhvV9"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
