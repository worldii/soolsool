package com.woowacamp.soolsool.fake;

import com.woowacamp.soolsool.global.aop.DistributedLock;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(value = 1)
@Profile("!test")
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @Autowired
    public DistributedLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(com.woowacamp.soolsool.global.aop.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        long waitTime = 100L;
        DistributedLock lock = method.getAnnotation(DistributedLock.class);

        RLock rLock = redissonClient.getLock(lock.lockName() + lock.entityId());

        try {
            if (!rLock.tryLock(waitTime, lock.leaseTime(), lock.timeUnit())) {
                return false;
            }
            Object returnValue = joinPoint.proceed();
            return returnValue;
        } catch (final Exception e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException();
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}
