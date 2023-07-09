package br.com.alura.estoque.http.services;

import java.util.List;

import br.com.alura.estoque.model.Produto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductService {

    @GET("produto")
    Call<List<Produto>> allProducts();
}
