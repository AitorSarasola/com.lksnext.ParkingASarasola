package com.lksnext.parkingplantilla.domain;

import java.util.List;

public interface CallbackList<T> {
    void onSuccess(List<T> lista);
    void onFailure(String errorM);
}
