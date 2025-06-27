package com.lksnext.parkingplantilla.domain;

public interface Callback {
    void onSuccess();
    void onFailure();
    void onFailure(String errorM);
}
