/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.xujun.library_algorithm.memory;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limited cache. Provides object storing. Size of all stored bitmaps will not to exceed size limit (
 * {@link #getSizeLimit()}).<br />
 * <br />
 * <b>NOTE:</b> This cache uses strong and weak references for stored Bitmaps. Strong references - for limited count of
 * Bitmaps (depends on cache size), weak references - for all other cached Bitmaps.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see BaseMemoryCache
 * @since 1.0.0
 */
public abstract class LimitedMemoryCache implements MemoryCache {

	private static final int MAX_NORMAL_CACHE_SIZE_IN_MB = 16;
	private static final int MAX_NORMAL_CACHE_SIZE = MAX_NORMAL_CACHE_SIZE_IN_MB * 1024 * 1024;

	private final int sizeLimit;
     public static final String TAG="tag";

	private final AtomicInteger cacheSize;

    private final Map<String, Bitmap> mMap= Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>());



	public LimitedMemoryCache(int sizeLimit) {
		this.sizeLimit = sizeLimit;

        cacheSize = new AtomicInteger();
		if (sizeLimit > MAX_NORMAL_CACHE_SIZE) {
			Log.w(TAG,"You set too large memory cache size (more than %1$d Mb)"+ MAX_NORMAL_CACHE_SIZE_IN_MB);
		}

	}

	@Override
	public boolean put(String key, Bitmap value) {
		boolean putSuccessfully = false;
		// Try to add value to hard cache
		int valueSize = getSize(value);
		int sizeLimit = getSizeLimit();
		int curCacheSize = cacheSize.get();
		if (valueSize < sizeLimit) {
			while (curCacheSize + valueSize > sizeLimit) {
                String removeKey = removeNext();
                if(removeKey==null){
                   break;
                }
                Bitmap bitmap = mMap.remove(key);
                if(bitmap!=null){
                    curCacheSize = cacheSize.addAndGet(-getSize(bitmap));
                }
			}
            mMap.put(key,value);
			cacheSize.addAndGet(valueSize);
			putSuccessfully = true;
		}

		return putSuccessfully;
	}

	@Override
	public Bitmap remove(String key) {
		return  mMap.remove(key);
	}

    @Override
    public Bitmap get(String key) {
        return mMap.get(key);
    }

    @Override
	public void clear() {
		mMap.clear();
		cacheSize.set(0);
	}

	protected int getSizeLimit() {
		return sizeLimit;
	}

    @Override
    public Collection<String> keys() {
        synchronized (mMap) {
            return new HashSet<String>(mMap.keySet());
        }
    }

	protected abstract int getSize(Bitmap value);

	protected abstract String removeNext();
}
