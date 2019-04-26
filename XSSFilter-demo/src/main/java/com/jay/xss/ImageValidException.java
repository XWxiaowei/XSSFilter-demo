package com.jay.xss;

/**
 * @author xiang.wei
 * @create 2019/4/25 19:26
 */
public class ImageValidException extends RuntimeException {
    public ImageValidException() {
        super();
    }

    public ImageValidException(String message) {
        super(message);
    }

    public ImageValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
