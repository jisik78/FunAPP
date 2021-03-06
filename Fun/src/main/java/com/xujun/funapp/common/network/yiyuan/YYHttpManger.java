package com.xujun.funapp.common.network.yiyuan;

import com.xujun.funapp.common.network.CustomException;
import com.xujun.funapp.common.network.HttpException;
import com.xujun.funapp.common.network.IRequestListener;
import com.xujun.funapp.common.network.RequestListener;
import com.xujun.funapp.common.util.WriteLogUtil;
import com.xujun.mylibrary.utils.ListUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author xujun  on 2016/12/24.
 * @email gdutxiaoxu@163.com
 */

public class YYHttpManger {

    public static final String TAG = "YYHttpManger";

    private YYHttpManger() {

    }

    public static YYHttpManger getInstance() {
        return Holder.mInstance;
    }

    private static class Holder {
        private static final YYHttpManger mInstance = new YYHttpManger();
    }

    public YiYuanApi getApi() {
        return YiYuanApiManger.getInstance().getApi(YiYuanApi.class, YiYuanApi.mBaseUrl);
    }

   /* public void excutePush(HashMap<String,Object> map,String url,BaseFunc1<String> baseFunc1){
        Observable<RequestBody> observable = getApi().excutePush(url, map);
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe();
    }*/

    public void push(String url, Map<String, Object> map, final IRequestListener iRequestListener) {
        ListUtils.getMapString(map);
        WriteLogUtil.i(TAG, " 请求参数是map=" + map);
        Observable<ResponseBody> observable = getApi().excutePush(url, map);
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).map(new Func1<ResponseBody, String>() {
            @Override
            public String call(ResponseBody responseBody) {
                try {
                    String result = responseBody.string();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {//这个操作是捕获Gson转化错误的
                    e.printStackTrace();
                }
                return "出错了，请查看";
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe
                (new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (iRequestListener != null) {
                            HttpException httpException = new HttpException(HttpException
                                    .CUSTOM_ERROR_CODE, e, e.getMessage());
                            iRequestListener.onFail(httpException);
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (iRequestListener == null) {
                            return;
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String showapi_res_error = jsonObject.getString("showapi_res_error");
                            //  请求成功
                            String showapi_res_code = jsonObject.getString("showapi_res_code");
                            if (isSuccess(showapi_res_code)) {
                                s = jsonObject.getString("showapi_res_body");
                                iRequestListener.onResponse(s);
                            } else {
                                HttpException httpException = new HttpException(HttpException
                                        .CUSTOM_ERROR_CODE, new CustomException(),
                                        showapi_res_error + "\n" + showapi_res_code);
                                iRequestListener.onFail(httpException);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private boolean isSuccess(String showapi_res_code) {
        return showapi_res_code.equals("0");
    }

    public void push(String url, Map<String, Object> map, Subscriber<String> subscriber) {
        Observable<ResponseBody> observable = getApi().excutePush(url, map);
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).map(new Func1<ResponseBody, String>() {
            @Override
            public String call(ResponseBody responseBody) {
                try {
                    String result = responseBody.string();
                    JSONObject jsonObject = new JSONObject(result);
                    //  请求成功
                    if (isSuccess(jsonObject.getString("showapi_res_code"))) {
                        result = jsonObject.getString("showapi_res_body");
                    }

                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {//这个操作是捕获Gson转化错误的
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "出错了，请查看";
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe
                (subscriber);

    }

    public void push(String url, Map<String, Object> map, final RequestListener<String>
            requestListener) {
        Observable<ResponseBody> observable = getApi().excutePush(url, map);
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).map(new Func1<ResponseBody, String>() {
            @Override
            public String call(ResponseBody responseBody) {
                try {
                    String result = responseBody.string();
                    JSONObject jsonObject = new JSONObject(result);
                    //  请求成功
                    String showapi_res_code = jsonObject.getString("showapi_res_code");
                    if (isSuccess(showapi_res_code)) {
                        result = jsonObject.getString("showapi_res_body");

                    }

                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {//这个操作是捕获Gson转化错误的
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "出错了，请查看";
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe
                (new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        requestListener.onError(e);
                    }

                    @Override
                    public void onNext(String s) {
                        if (requestListener != null) {
                            requestListener.onSuccess(s);
                        }
                    }
                });

    }

    public void excutePush(HashMap<String, Object> map, String url, Subscriber<ResponseBody>
            subscriber) {
        /*Observable<RequestBody> observable = getApi().excutePush(url, map);
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).map(new Func1<RequestBody, String>() {
            @Override
            public String call(RequestBody requestBody) {
                String s = requestBody.toString();
                return s;
            }
        }).subscribe(subscriber);*/
        Observable<ResponseBody> observable = getApi().excutePush(url, map);
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(subscriber);

    }

    public <T> Subscription doHttpDeal(Observable<T> observable, Subscriber<T> subscriber) {
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io()).subscribe(subscriber);
    }

    public static class LiftAllTransformer<T, R> implements Observable.Transformer<T, R> {

        @Override
        public Observable<R> call(Observable<T> observable) {
            return (Observable<R>) observable.subscribeOn(Schedulers.io()).// 指定在IO线程执行耗时操作
                    unsubscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread());//指定Subscrible在主线程回调
        }
    }


}
