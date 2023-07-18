package br.com.alura.estoque.repository.callback;

public interface BaseCallbackListener<T> {
    void onSuccess(T data);

    void onError(String message);
}
