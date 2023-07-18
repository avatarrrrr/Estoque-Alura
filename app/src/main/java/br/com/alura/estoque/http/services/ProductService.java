package br.com.alura.estoque.http.services;

import java.util.List;

import br.com.alura.estoque.model.Produto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductService {
    String PRODUCT_KEYWORD = "produto";

    @GET(PRODUCT_KEYWORD)
    Call<List<Produto>> allProducts();

    @POST(PRODUCT_KEYWORD)
    Call<Produto> createProduct(@Body Produto product);

    @PUT(PRODUCT_KEYWORD + "/{id}")
    Call<Produto> editProduct(@Path("id") long productIdentifier, @Body Produto product);
}
