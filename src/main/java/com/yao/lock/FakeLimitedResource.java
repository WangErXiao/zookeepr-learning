package com.yao.lock;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by root on 15-2-8.
 */
public class FakeLimitedResource {
    private final AtomicBoolean inUse=new AtomicBoolean(false);

    public void use() throws InterruptedException {
        if(!inUse.compareAndSet(false,true)){
            throw  new IllegalStateException("Needs to be used by one client at a time");
        }
        try{
            TimeUnit.SECONDS.sleep(3);
        }finally {
            inUse.set(false);
        }
    }
}
