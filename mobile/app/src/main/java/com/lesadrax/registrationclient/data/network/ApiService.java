package com.lesadrax.registrationclient.data.network;

import com.lesadrax.registrationclient.data.model.ActorListModel;
import com.lesadrax.registrationclient.data.model.AuthenticateResponse;
import com.lesadrax.registrationclient.data.model.AuthenticateUinResponse;
import com.lesadrax.registrationclient.data.model.AuthenticationData;
import com.lesadrax.registrationclient.data.model.LoginRequest;
import com.lesadrax.registrationclient.data.model.LoginResponse;
import com.lesadrax.registrationclient.data.model.OperationModel;
import com.lesadrax.registrationclient.data.model.PagedResponse;
import com.lesadrax.registrationclient.data.model.SynchroData;
import com.lesadrax.registrationclient.data.model.SynchroResponse;
import com.lesadrax.registrationclient.data.model.UinDetailRequest;
import com.lesadrax.registrationclient.data.model.UinDetailResponse;
import com.lesadrax.registrationclient.data.model.UpdateModel;
import com.lesadrax.registrationclient.data.model.UpdateResponse;
import com.lesadrax.registrationclient.data.model.User;
import com.lesadrax.registrationclient.data.model.UserResponse;

import java.util.LinkedHashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/auth/signin")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("land-reg/api/v1/actors/bio-auth") // Remplacez "your-endpoint" par l'URL relative de votre API
    Call<AuthenticateResponse> authenticate(@Body AuthenticationData authenticationData);

    @POST("land-reg/api/v1/synchro-histories/init-synchro") // Remplacez par le chemin correct de votre API
    Call<SynchroResponse> sendSynchroData(
            @Body SynchroData synchroData // L'objet envoyé en paramètre
    );

    @PATCH("land-reg/api/v1/synchro-histories/finish-synchro/{batch-number}")
    Call<SynchroResponse> finishSynchro(
            @Path("batch-number") String batchNumber // Paramètre dans l'URL
    );

    @POST("land-reg/api/v1/actors")
    Call<Void> createActor(
            @Body Object actorJson // JSON à envoyer dans le corps
    );


    @POST("land-reg/api/v1/constatations")
    Call<Void> createOperation(
            @Body Object operationJson // JSON à envoyer dans le corps
    );

    @GET("land-reg/api/v1/actors/by-status?status=ACTOR")
    Call<PagedResponse<ActorListModel>> getActors(
            @Query("page") int page,         // Numéro de la page
            @Query("size") int size          // Taille de la page
    );

    @GET("land-reg/api/v1/constatations")
    Call<PagedResponse<OperationModel>> getOperations(
            @Query("page") int page,         // Numéro de la page
            @Query("size") int size          // Taille de la page
    );

    @GET("land-reg/api/v1/actors/by-status?status=PENDING")
    Call<PagedResponse<ActorListModel>> getPendingActors(
            @Query("page") int page,         // Numéro de la page
            @Query("size") int size          // Taille de la page
    );

    @GET("land-reg/api/v1/actors/{id}")
    Call<ResponseBody> getActor(@Path("id") long id);

    @GET("api/v1/users/all") // Chemin vers ton endpoint pour récupérer les utilisateurs
    Call<UserResponse<User>> getUsers();


    @GET("land-reg/api/v1/constatations/{id}")
    Call<ResponseBody> getOp(@Path("id") long id);

    @POST("land-reg/api/v1/actors/uin-details")
    Call<UinDetailResponse> uinDetailInfo(@Body UinDetailRequest request);

    @PUT("land-reg/api/v1/actors/{id}")
    Call<LinkedHashMap<String, Object>> updateObject(
            @Path("id") String id,
            @Body Object requestBody
    );

    @GET("land-reg/api/v1/actors/uin/{uin}")
    Call<AuthenticateUinResponse> getActorByUin(@Path("uin") String uin);

    @PUT("land-reg/api/v1/constatations")
    Call<LinkedHashMap<String, Object>> updateConstatation(
            @Body Object requestBody
    );


}
