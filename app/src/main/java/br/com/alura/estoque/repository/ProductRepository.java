package br.com.alura.estoque.repository;

import androidx.annotation.NonNull;

import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.http.RetrofitHttpSingleton;
import br.com.alura.estoque.http.services.ProductService;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.callback.BaseCallback;
import br.com.alura.estoque.repository.callback.BaseCallbackListener;
import retrofit2.Call;

public class ProductRepository {

    private final ProdutoDAO dao;
    private final ProductService productService;

    public ProductRepository(ProdutoDAO dao) {
        this.dao = dao;
        productService = RetrofitHttpSingleton.getProductService();
    }

    public void searchProducts(ProductRepositoryListener<List<Produto>> listener) {
        searchOnInternalStorage(listener);
    }

    private void searchOnInternalStorage(ProductRepositoryListener<List<Produto>> listener) {
        new BaseAsyncTask<>(dao::buscaTodos, result -> {
            listener.onSuccess(result);
            searchProductsOnline(listener);
        }).execute();
    }

    public void save(Produto produto, ProductRepositoryListener<Produto> listener) {
        saveOnline(produto, new ProductRepositoryListener<Produto>() {
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

    public void edit(Produto product, ProductRepositoryListener<Produto> listener) {
        editProductOnline(product, listener);
    }

    private void editProductOnline(Produto product, ProductRepositoryListener<Produto> listener) {
        final Call<Produto> editProductCall = productService.editProduct(product.getId(), product);
        editProductCall.enqueue(new BaseCallback(new BaseCallbackListener<Produto>() {
            @Override
            public void onSuccess(Produto data) {
                editOnInternalStorage(data, listener);
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        }));
    }

    private void editOnInternalStorage(Produto product, ProductRepositoryListener<Produto> listener) {
        new BaseAsyncTask<>(() -> {
            dao.atualiza(product);
            return product;
        }, listener::onSuccess).execute();
    }

    private void searchProductsOnline(@NonNull ProductRepositoryListener<List<Produto>> listener) {
        final Call<List<Produto>> allProductsCall = productService.allProducts();
        allProductsCall.enqueue(new BaseCallback(new BaseCallbackListener<List<Produto>>() {
            @Override
            public void onSuccess(List<Produto> data) {
                new BaseAsyncTask<>(() -> {
                    dao.saveAll(data);
                    return dao.buscaTodos();
                }, listener::onSuccess).execute();
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        }));

    }

    private void saveOnline(Produto produto, ProductRepositoryListener<Produto> listener) {
        final Call<Produto> createProductCall = productService.createProduct(produto);
        createProductCall.enqueue(new BaseCallback(new BaseCallbackListener<Produto>() {
            @Override
            public void onSuccess(Produto data) {
                listener.onSuccess(data);
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        }));
    }

    private void saveOnInternalStorage(Produto produto, @NonNull ProductRepositoryListener<Produto> listener) {
        new BaseAsyncTask<>(() -> {
            long id = dao.save(produto);
            return dao.findProductById(id);
        }, listener::onSuccess).execute();
    }

    public interface ProductRepositoryListener<T> {
        void onSuccess(T data);

        void onError(String message);
    }
}
