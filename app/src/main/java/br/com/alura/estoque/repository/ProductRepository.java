package br.com.alura.estoque.repository;

import java.io.IOException;
import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.http.RetrofitHttpSingleton;
import br.com.alura.estoque.model.Produto;
import retrofit2.Call;

public class ProductRepository {

    private final ProdutoDAO dao;

    public ProductRepository(ProdutoDAO dao) {
        this.dao = dao;
    }

    public void searchProductsOnInternalStorage(Listener listener) {
        new BaseAsyncTask<>(dao::buscaTodos, resultado -> {
            listener.call(resultado);
            searchProductsOnline(listener);
        }).execute();
    }

    private void searchProductsOnline(Listener listener) {
        Call<List<Produto>> allProductsCall = RetrofitHttpSingleton.getProductService().allProducts();
        new BaseAsyncTask<>(() -> {
            try {
                dao.saveAll(allProductsCall.execute().body());
            } catch (IOException ignored) {
            }
            return dao.buscaTodos();
        }, listener::call).execute();
    }

    public interface Listener {
        void call(List<Produto> data);
    }
}
