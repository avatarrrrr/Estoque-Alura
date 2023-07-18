package br.com.alura.estoque.repository;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.http.RetrofitHttpSingleton;
import br.com.alura.estoque.http.services.ProductService;
import br.com.alura.estoque.model.Produto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class ProductRepository {

    private final ProdutoDAO dao;
    private final ProductService productService;

    public ProductRepository(ProdutoDAO dao) {
        this.dao = dao;
        productService = RetrofitHttpSingleton.getProductService();
    }

    public void searchProductsOnInternalStorage(Listener<List<Produto>> listener) {
        new BaseAsyncTask<>(dao::buscaTodos, resultado -> {
            listener.call(resultado);
            searchProductsOnline(listener);
        }).execute();
    }

    private void searchProductsOnline(@NonNull Listener<List<Produto>> listener) {
        final Call<List<Produto>> allProductsCall = productService.allProducts();
        new BaseAsyncTask<>(() -> {
            try {
                dao.saveAll(allProductsCall.execute().body());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return dao.buscaTodos();
        }, listener::call).execute();
    }

    public void save(Produto produto, ListenerOnSuccessAndOnError<Produto> listener) {
        saveOnline(produto, new ListenerOnSuccessAndOnError<Produto>() {
            @Override
            public void onSuccess(Produto data) {
                saveOnInternalStorage(data, listener);
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }

    private void saveOnline(Produto produto, ListenerOnSuccessAndOnError<Produto> listener) {
        final Call<Produto> createProductCall = productService.createProduct(produto);
        createProductCall.enqueue(new Callback<Produto>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<Produto> call, Response<Produto> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onError(response.message());
                    }
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<Produto> call, Throwable t) {
                listener.onError(t.getLocalizedMessage());
            }
        });
    }

    private void saveOnInternalStorage(Produto produto, @NonNull ListenerOnSuccessAndOnError<Produto> listener) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produto);
            return dao.buscaProduto(id);
        }, listener::onSuccess).execute();
    }

    public interface Listener<T> {
        void call(T data);
    }

    public interface ListenerOnSuccessAndOnError<T> {
        void onSuccess(T data);

        void onError(String message);
    }
}
