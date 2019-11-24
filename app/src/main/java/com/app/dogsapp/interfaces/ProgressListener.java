package com.app.dogsapp.interfaces;

public interface ProgressListener {

    public void loadingFinished();
    public void showError(String error);
    public void showToastMessage(String message);
    public void showGeneralError();
    public void loadingStarted();
    public void showNoInternetConenction();


}
