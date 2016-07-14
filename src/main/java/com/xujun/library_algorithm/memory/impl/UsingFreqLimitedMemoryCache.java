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


public class UsingFreqLimitedMemoryCache extends LimitedMemoryCache {

	private final Map<String, Integer> usingCounts = Collections.synchronizedMap(new HashMap<String, Integer>());

	public UsingFreqLimitedMemoryCache(int sizeLimit) {
		super(sizeLimit);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		if (super.put(key, value)) {
			usingCounts.put(key, 0);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Bitmap get(String key) {
		Bitmap value = super.get(key);
		// Increment usage count for value if value is contained in hardCahe
		if (value != null) {
			Integer usageCount = usingCounts.get(value);
			if (usageCount != null) {
				usingCounts.put(key, usageCount + 1);
			}
		}
		return value;
	}

	@Override
	public Bitmap remove(String key) {
        usingCounts.remove(key);
		return super.remove(key);
	}



    @Override
	public void clear() {
		usingCounts.clear();
		super.clear();
	}

	@Override
	protected int getSize(Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected String removeNext() {
		Integer minUsageCount = null;
        String leastUsedValue = null;
		Set<Entry<String, Integer>> entries = usingCounts.entrySet();
		synchronized (usingCounts) {
			for (Entry<String, Integer> entry : entries) {
				if (leastUsedValue == null) {
					leastUsedValue = entry.getKey();
					minUsageCount = entry.getValue();
				} else {
					Integer lastValueUsage = entry.getValue();
					if (lastValueUsage < minUsageCount) {
						minUsageCount = lastValueUsage;
						leastUsedValue = entry.getKey();
					}
				}
			}
		}
		usingCounts.remove(leastUsedValue);
		return leastUsedValue;
	}

}
