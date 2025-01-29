package com.example.test.demo.service.interfaces;

public interface CacheService {
    /**
     * Вместо базы данных было решено использовать такое внутреннее хранилище
     * с целью экономии ресурсов и исключения частых обращений к базе на каждый ход.
     */
    void cleanCacheByTime();

}
