package de.ait.g_67_shop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component // Объект станет spring бином
public class AspectLogging {

    private final Logger logger = LoggerFactory.getLogger(AspectLogging.class);

    // Pointcut, по сути, описывает набор правил, а где будут у нас join point,
    // то есть те точки в коде, куда будет применяться дополнительная логика.
    // * - Значит привязка ко всем методам этого класса
    // .. - искать методы в текущем пакете и во всех его подпакетах.
    @Pointcut("execution(* de.ait.g_67_shop.service..*.*(..))")
    public void anyMethodInServicePackage() {
    }

    @Before("anyMethodInServicePackage()")
    public void beforeAnyMethod(JoinPoint joinPoint) {
        // getName() - возвращает имя вызванного метода
        String methodName = joinPoint.getSignature().getName();
        // Динамически получаем имя текущего класса сервиса вместо жесткого текста
        String className = joinPoint.getTarget().getClass().getSimpleName();
        // Метод getArgs возвращает нам массив аргументов, которые реально были переданы в метод
        Object[] args = joinPoint.getArgs();
        logger.debug("Method {} of the class {} called with arguments: {}",
                methodName, className, Arrays.toString(args));
    }

    /*
    Нужно аккуратно относиться к логированию аргументов.
    1. Аргумент может быть очень большим объектом.
    2. Аргумент может содержать секреты.
     */

    // Вместо @After более информативно применять связку @AfterReturning + @AfterThrowling

    @AfterReturning(
            pointcut = "anyMethodInServicePackage()",
            returning = "result"
    )
    public void afterReturningAnyMethodInProductService(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.debug("Method {} of the class {} called with args {} returned result: {}", methodName, className, Arrays.toString(joinPoint.getArgs()), result);
    }

    @AfterThrowing(
            pointcut = "anyMethodInServicePackage()",
            throwing = "e"
    )
    public void afterThrowingAnyMethodInProductService(JoinPoint joinPoint, Exception e) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.warn("Method {} of the class {} called with args {} threw {}: {}",
                methodName, className, Arrays.toString(joinPoint.getArgs()),
                e.getClass().getSimpleName(), e.getMessage(), e);
    }

}
