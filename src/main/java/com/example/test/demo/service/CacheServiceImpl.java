package com.example.test.demo.service;

import com.example.test.demo.model.GameModel;
import com.example.test.demo.service.interfaces.CacheService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheServiceImpl implements CacheService {

   public static final Map<UUID, GameModel> LOCAL_CACHE = new ConcurrentHashMap<>();

   private static final long TIME_TO_CLEAR = 5*60*60*1000;//Каждые пять часов

    @Override
    @Scheduled(fixedRate = TIME_TO_CLEAR)
    public void cleanCacheByTime() {
     LOCAL_CACHE.clear();
    }

}
