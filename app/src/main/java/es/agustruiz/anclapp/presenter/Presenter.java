package es.agustruiz.anclapp.presenter;

import android.view.View;

public interface Presenter {
    void showMessage(View view, String message);
    void showMessage(String message);
}
