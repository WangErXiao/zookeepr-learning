package com.yao.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.concurrent.TimeUnit;

/**
 * Created by root on 15-2-8.
 */
public class ReadWriteLock {
    private final InterProcessReadWriteLock lock;
    private final InterProcessMutex readLock;
    private final InterProcessMutex writeLock;
    public ReadWriteLock(CuratorFramework client,
                                       String lockPath) {
        lock = new InterProcessReadWriteLock(client, lockPath);
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }
    public boolean acquireWriteLock(long time,TimeUnit unit) throws Exception {
         return writeLock.acquire(time,unit);
    }
    public void acquireWriteLock() throws Exception {
         writeLock.acquire();
    }
    public void releaseWriteLock() throws Exception {
         writeLock.release();
    }
    public boolean acquireReadLock(long time,TimeUnit unit) throws Exception {
        return readLock.acquire(time,unit);
    }
    public void acquireReadLock() throws Exception {
        readLock.acquire();
    }
    public void releaseReadLock() throws Exception {
        readLock.release();
    }

}
