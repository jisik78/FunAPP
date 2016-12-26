package com.xujun.myrxretrofitlibrary;

import java.util.HashMap;

import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author xujun  on 2016/12/24.
 * @email gdutxiaoxu@163.com
 */

public class YYHttpManger {

    private YYHttpManger() {

    }

    public static YYHttpManger getInstance() {
        return Holder.mInstance;
    }

    private static class Holder {
        private static final YYHttpManger mInstance = new YYHttpManger();
    }

    public YiYuanApi getApi(){
        return YiYuanApiManger.getInstance().getApi(YiYuanApi.class,YiYuanApi.mBaseUrl);
    }

   /* public void excutePush(HashMap<String,Object> map,String url,BaseFunc1<String> baseFunc1){
        Observable<RequestBody> observable = getApi().excutePush(url, map);
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe();
    }*/

    public void excutePush(HashMap<String,Object> map,String url,Subscriber<RequestBody> subscriber){
        /*Observable<RequestBody> observable = getApi().excutePush(url, map);
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).map(new Func1<RequestBody, String>() {
            @Override
            public String call(RequestBody requestBody) {
                String s = requestBody.toString();
                return s;
            }
        }).subscribe(subscriber);*/
        Observable<RequestBody> observable = getApi().excutePush(url, map);
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);

    }

    public <T> Subscription doHttpDeal(Observable<T> observable, Subscriber<T> subscriber) {
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io()).subscribe(subscriber);
    }

    public static class LiftAllTransformer<T,R> implements Observable.Transformer<T, R> {


        @Override
        public Observable<R> call(Observable<T> observable) {
            return (Observable<R>)observable.subscribeOn(Schedulers.io()).// 指定在IO线程执行耗时操作
                    unsubscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread());//指定Subscrible在主线程回调
        }
    }


}
