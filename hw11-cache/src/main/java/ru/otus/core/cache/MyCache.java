package ru.otus.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.service.DbServiceClientImpl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {

    private final String PUT_OPERATION_CODE = "PUT_OPERATION";

    private final String REMOVE_OPERATION_CODE = "REMOVE_OPERATION";

    private final String GET_OPERATION_CODE = "GET_OPERATION";

    private final String CLEAR_OPERATION_CODE = "CLEAR_OPERATION";
    private final int defaultCapacity = 1000;

    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);
    private final Map<K, V> storage = new WeakHashMap<>();

    private final List<WeakReference<HwListener<K, V>>> listeners = new ArrayList<>();

    private final int capacity;

    public MyCache(int capacity) {
        this.capacity = capacity;
    }

    public MyCache() {
        this.capacity = defaultCapacity;
    }

    public CacheState getCacheState() {
        return new CacheState(storage.size(), listeners.size());
    }

    @Override
    public void put(K key, V value) {
        log.info("Called operation {} , k={}; v={}", PUT_OPERATION_CODE, key, value);
        if (capacity <= storage.size()) {
            storage.clear();
            notify(null, null, CLEAR_OPERATION_CODE);
        }
        storage.put(key, value);
        notify(key, value, PUT_OPERATION_CODE);
    }

    @Override
    public void remove(K key) {
        var value = storage.remove(key);
        log.info("Called operation {} , k={}; v={}", REMOVE_OPERATION_CODE, key, value);
        notify(key, value, REMOVE_OPERATION_CODE);
    }

    @Override
    public V get(K key) {
        V value = storage.get(key);
        log.info("Called operation {} , k={}; v={}", GET_OPERATION_CODE, key, value);
        notify(key, value, GET_OPERATION_CODE);
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        List<WeakReference<HwListener<K, V>>> refsForRemove = new ArrayList<>();
        listeners.forEach(l -> {
            var currentListener = l.get();
            if (currentListener == null || listener.equals(currentListener)) {
                refsForRemove.add(l);
            }
        });
        listeners.removeAll(refsForRemove);
    }

    private void notify(K key, V value, String code) {
        List<WeakReference<HwListener<K, V>>> emptyRefs = new ArrayList<>();
        listeners.forEach(listener -> {
            try {
                listener.get().notify(key, value, code);
            } catch (NullPointerException e) {
                log.error("Found removed listener");
                emptyRefs.add(listener);
            }
        });
        listeners.removeAll(emptyRefs);
    }

    public record CacheState(int cacheSize, int listenerCount) {

    }
}
