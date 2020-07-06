package com.android.upload;

import java.io.IOException;

public interface OnUploadListener {

    void onUploadProgress(UploadResponse response, long contentLength, long progress);

    void onUploadFailure(UploadResponse response, IOException e);

    void onUploadResponse(UploadResponse response);

}
