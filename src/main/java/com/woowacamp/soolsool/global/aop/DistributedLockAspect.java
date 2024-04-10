package com.woowacamp.soolsool.global.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

@Aspect
@RequiredArgsConstructor
@Order(value = 1)
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.woowacamp.soolsool.global.aop.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        DistributedLock lock = method.getAnnotation(DistributedLock.class);

        RLock rLock = redissonClient.getLock(lock.lockName() + lock.entityId());

        try {
            if (!rLock.tryLock(lock.waitTime(), lock.leaseTime(), lock.timeUnit())) {
                return false;
            }
            return aopForTransaction.proceed(joinPoint);
        } catch (final Exception e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException();
        } finally {
            rLock.unlock();
        }
    }
}
