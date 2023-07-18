package br.com.alura.estoque.repository.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class BaseCallback implements Callback {
    private final BaseCallbackListener listener;

    public BaseCallback(BaseCallbackListener listener) {
        this.listener = listener;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call call, Response response) {
        if (response.isSuccessful()) {
            if (response.body() != null) {
                listener.onSuccess(response.body());
            } else {
                listener.onError("Body is null!");
            }
        } else {
            listener.onError(response.message());
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call call, Throwable t) {
        listener.onError(t.getLocalizedMessage());
    }
}