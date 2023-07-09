package br.com.alura.estoque.http;

import br.com.alura.estoque.http.services.ProductService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHttpSingleton {
    private static final String BASE_URL = "http://192.168.100.51:8080/";
    private static final Retrofit retrofitInstance;

    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();


        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BASE_URL);
        builder.client(client);
        builder.addConverterFactory(GsonConverterFactory.create());
        retrofitInstance = builder.build();
    }

    private static ProductService productService;

    private RetrofitHttpSingleton() {
    }

    public static ProductService getProductService() {
        if (productService == null) productService = retrofitInstance.create(ProductService.class);
        return productService;
    }
}
