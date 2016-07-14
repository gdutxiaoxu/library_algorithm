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
package com.xujun.library_algorithm.memory.impl;

import android.graphics.Bitmap;

import com.xujun.library_algorithm.memory.LimitedMemoryCache;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class LargestLimitedMemoryCache extends LimitedMemoryCache {

	private final Map<String, Integer> valueSizes = Collections.synchronizedMap(new HashMap<String, Integer>());

	public LargestLimitedMemoryCache(int sizeLimit) {
		super(sizeLimit);
	}

	@Override
    public boolean put(String key, Bitmap value) {
        if (super.put(key, value)) {
            valueSizes.put(key, getSize(value));
            return true;
        } else {
            return false;
        }
    }

	@Override
	public Bitmap remove(String key) {
        valueSizes.remove(key);
		return super.remove(key);
	}

	@Override
	public void clear() {
		valueSizes.clear();
		super.clear();
	}

	@Override
	protected int getSize(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected String removeNext() {
		Integer maxSize = null;
		String largestValue = null;
		Set<Entry<String, Integer>> entries = valueSizes.entrySet();
		synchronized (valueSizes) {
			for (Entry<String, Integer> entry : entries) {
				if (largestValue == null) {
					maxSize = entry.getValue();
				} else {
					Integer size = entry.getValue();
					if (size > maxSize) {
						maxSize = size;
						largestValue = entry.getKey();
					}
				}
			}
		}
		valueSizes.remove(largestValue);
		return largestValue;
	}

}
