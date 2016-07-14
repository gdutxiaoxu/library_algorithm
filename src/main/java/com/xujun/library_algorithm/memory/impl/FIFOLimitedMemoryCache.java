/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.xujun.library_algorithm.memory.impl;

import android.graphics.Bitmap;

import com.xujun.library_algorithm.memory.LimitedMemoryCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FIFOLimitedMemoryCache extends LimitedMemoryCache {

    private final List<String> mStringQueue = Collections.synchronizedList(new ArrayList<String>());

    public FIFOLimitedMemoryCache(int sizeLimit) {
        super(sizeLimit);
    }

    @Override
    public boolean put(String key, Bitmap value) {
        if (super.put(key, value)) {
            mStringQueue.add(key);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Bitmap remove(String key) {
        mStringQueue.remove(key);
        return super.remove(key);
    }

    @Override
    public void clear() {
        mStringQueue.clear();
        super.clear();
    }

    @Override
    protected int getSize(Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    protected String removeNext() {
        return mStringQueue.remove(0);
    }

}
