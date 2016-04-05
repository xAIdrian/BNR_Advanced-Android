package com.bignerdranch.android.networkingarchitecture.web;

import com.bignerdranch.android.networkingarchitecture.exception.UnauthorizedException;

import java.net.HttpURLConnection;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amohnacs on 4/4/16.
 */
public class RetrofitErrorHandler implements ErrorHandler{


    /**
     * Return a custom exception to be thrown for a {@link RetrofitError}. It is recommended that you
     * pass the supplied error as the cause to any new exceptions.
     * <p/>
     * If the return exception is checked it must be declared to be thrown on the interface method.
     * <p/>
     * Example usage:
     * <pre>
     * class MyErrorHandler implements ErrorHandler {
     *   &#64;Override public Throwable handleError(RetrofitError cause) {
     *     Response r = cause.getResponse();
     *     if (r != null &amp;&amp; r.getStatus() == 401) {
     *       return new UnauthorizedException(cause);
     *     }
     *     return cause;
     *   }
     * }
     * </pre>
     *
     * @param cause the original {@link RetrofitError} exception
     * @return Throwable an exception which will be thrown from a synchronous interface method or
     * passed to an asynchronous error callback. Must not be {@code null}.
     */
    @Override
    public Throwable handleError(RetrofitError cause) {

        Response response = cause.getResponse();
        if(response != null && response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return new UnauthorizedException(cause);
        }
        return cause;
    }
}
