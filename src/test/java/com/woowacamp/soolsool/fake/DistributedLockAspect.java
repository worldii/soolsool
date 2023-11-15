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

@Aspect
@Order(value = 1)
@Profile("test")
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private static final long DEFAULT_WAIT_TIME = 100L;

    @Autowired
    public DistributedLockAspect(final RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(com.woowacamp.soolsool.global.aop.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final DistributedLock lock = method.getAnnotation(DistributedLock.class);
        final RLock rLock = redissonClient.getLock(lock.lockName() + lock.entityId());

        try {
            if (!rLock.tryLock(DEFAULT_WAIT_TIME, lock.leaseTime(), lock.timeUnit())) {
                return false;
            }
            return joinPoint.proceed();

        } catch (final Exception e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException();
        } finally {
            rLock.unlock();
        }
    }
}
