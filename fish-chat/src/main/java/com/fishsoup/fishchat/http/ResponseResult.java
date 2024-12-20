package com.fishsoup.fishchat.http;

import com.fishsoup.fishchat.enums.ResponseCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

import static com.fishsoup.fishchat.enums.ResponseCodeEnum.FAILURE;
import static com.fishsoup.fishchat.enums.ResponseCodeEnum.SUCCESS;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -2630866164174490014L;

    /** 0: 成功, 1: 异常 */
    private String code;

    private String msg;

    private Object data;

    public ResponseResult setStatus(ResponseCodeEnum responseCodeEnum) {
        this.code = responseCodeEnum.getCode();
        this.msg = responseCodeEnum.getDesc();
        return this;
    }

    public static ResponseResult success() {
        ResponseResult responseResult = new ResponseResult();
        return responseResult.setStatus(SUCCESS);
    }

    public static ResponseResult success(Object data) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setStatus(SUCCESS);
        responseResult.setData(data);
        return responseResult;
    }

    public static ResponseResult success(String msg, Object data) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(SUCCESS.getCode());
        responseResult.setMsg(msg);
        responseResult.setData(data);
        return responseResult;
    }

    public static ResponseResult error() {
        ResponseResult responseResult = new ResponseResult();
        return responseResult.setStatus(FAILURE);
    }

    public static ResponseResult error(String msg) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(FAILURE.getCode());
        responseResult.setMsg(msg);
        return responseResult;
    }
}
