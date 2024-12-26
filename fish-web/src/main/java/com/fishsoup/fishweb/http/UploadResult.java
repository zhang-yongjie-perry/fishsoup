package com.fishsoup.fishweb.http;

import com.fishsoup.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResult {

    private StatusEnum errno;
    private UploadResultData data;
    private String message;

    @Data
    @AllArgsConstructor
    private class UploadResultData {
        private String url;
        private String alt;
        private String href;
    }

    private UploadResult() {

    }

    private UploadResultData createUploadResultData(String url, String alt, String href) {
        return new UploadResultData(url, alt, href);
    }

    public static UploadResult success(String url, String alt, String href) {
        UploadResult result = new UploadResult();
        result.setErrno(StatusEnum.SUCCESS);
        result.setData(result.createUploadResultData(url, alt, href));
        return result;
    }

    public static UploadResult error(String msg) {
        return new UploadResult(StatusEnum.FAILURE, null, msg);
    }
}
