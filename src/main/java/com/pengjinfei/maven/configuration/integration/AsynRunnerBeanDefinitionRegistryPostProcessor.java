package com.pengjinfei.maven.configuration.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.ServiceActivatorFactoryBean;
import org.springframework.integration.gateway.GatewayMethodMetadata;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.integration.handler.DelayHandler;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.handler.advice.SpelExpressionRetryStateGenerator;
import org.springframework.integration.store.MessageGroupQueue;
import org.springframework.integration.transaction.DefaultTransactionSynchronizationFactory;
import org.springframework.integration.transaction.ExpressionEvaluatingTransactionSynchronizationProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Component
@Slf4j
public class AsynRunnerBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (String s : registry.getBeanDefinitionNames()) {
            try {
                BeanDefinition beanDefinition = registry.getBeanDefinition(s);
                String beanClassName = beanDefinition.getBeanClassName();
                if (beanClassName == null) {
                    continue;
                }
                Class<?> aClass = ClassUtils.forName(beanClassName, ClassUtils.getDefaultClassLoader());
                Service service = AnnotationUtils.findAnnotation(aClass, Service.class);
                if (service == null) {
                    continue;
                }
                ReflectionUtils.doWithLocalMethods(aClass, method -> {
                    AsynRunner asynRunner = AnnotationUtils.findAnnotation(method, AsynRunner.class);
                    if (asynRunner == null) {
                        return;
                    }
                    String methodName = method.getName();
                    int parameterCount = method.getParameterCount();
                    if (parameterCount != 1) {
                        log.warn("find @AsynRunner on class {} method {}, but parameters number is not 1.",
                                aClass.getName(),
                                methodName);
                        return;
                    }

                    String baseName = s + "_" + methodName;

                    //queue
                    String channelQueueId = baseName + "_queue";
                    RootBeanDefinition messageGroupQueueDef = new RootBeanDefinition(MessageGroupQueue.class);
                    ConstructorArgumentValues messageGroupQueueConArgs = messageGroupQueueDef.getConstructorArgumentValues();
                    RuntimeBeanReference channelMessageStoreRef = new RuntimeBeanReference("channelMessageStore");
                    messageGroupQueueConArgs.addIndexedArgumentValue(0, channelMessageStoreRef);
                    messageGroupQueueConArgs.addIndexedArgumentValue(1, baseName);

                    //channel
                    String channelId = baseName + "_channel";
                    RootBeanDefinition channelDef = new RootBeanDefinition(QueueChannel.class);
                    channelDef.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference(channelQueueId));

                    String retryCron = asynRunner.retryCron();
                    boolean retry = StringUtils.hasLength(retryCron);
                    if (retry) {
                        ManagedList managedList = new ManagedList();
                        GenericBeanDefinition retryIntceptorDef = new GenericBeanDefinition();
                        retryIntceptorDef.setBeanClass(RetryIdChannelInterceptor.class);
                        String insertSqlId = asynRunner.insertSqlId();
                        if (StringUtils.hasText(insertSqlId)) {
                            retryIntceptorDef.getPropertyValues().add("sql", insertSqlId);
                        }
                        managedList.add(retryIntceptorDef);
                        channelDef.getPropertyValues().add("interceptors",managedList);
                    }

                    //delayQueue
                    String delayChannelQueueId = baseName + "_delayQueue";
                    RootBeanDefinition delayChannelQueueDef = new RootBeanDefinition(MessageGroupQueue.class);
                    ConstructorArgumentValues delayChannelQueueConArgs = delayChannelQueueDef.getConstructorArgumentValues();
                    delayChannelQueueConArgs.addIndexedArgumentValue(0, new RuntimeBeanReference("channelMessageStore"));
                    delayChannelQueueConArgs.addIndexedArgumentValue(1, baseName+"_delay");

                    //delayChannel
                    String delayChannelId = baseName + "_delayChannel";
                    RootBeanDefinition delayChannelDef = new RootBeanDefinition(QueueChannel.class);
                    delayChannelDef.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference(delayChannelQueueId));

                    //gateway
                    //TODO 批量 gateway
                    String gateWaylId = s + "Gateway";
                    MutablePropertyValues gateWayPV;
                    if (!registry.containsBeanDefinition(gateWaylId)) {
                        BeanDefinition gateWayBeanDef = new RootBeanDefinition(GatewayProxyFactoryBean.class);
                        Class<?> serviceInterface = determineInterface(aClass, s);
                        if (serviceInterface == null) {
                            return;
                        }
                        gateWayPV = gateWayBeanDef.getPropertyValues();
                        gateWayPV.add("serviceInterface", serviceInterface);
                        registry.registerBeanDefinition(gateWaylId, gateWayBeanDef);
                    } else {
                        gateWayPV = registry.getBeanDefinition(gateWaylId).getPropertyValues();
                    }
                    Map<String, GatewayMethodMetadata> methodMetadataMap = new HashMap<>();
                    GatewayMethodMetadata methodMetadata = new GatewayMethodMetadata();
                    methodMetadata.setRequestChannelName(channelId);
                    methodMetadataMap.put(methodName, methodMetadata);
                    gateWayPV.add("methodMetadataMap", methodMetadataMap);

                    //retry
                    GenericBeanDefinition retryAdviceDef = new GenericBeanDefinition();
                    retryAdviceDef.setBeanClass(RequestHandlerRetryAdvice.class);
                    MutablePropertyValues retryAdvicePV = retryAdviceDef.getPropertyValues();
                    //TODO 自定义重试次数
                    retryAdvicePV.add("retryTemplate", new RuntimeBeanReference("retryTemplate"));

                    GenericBeanDefinition stateGeneratorDef = new GenericBeanDefinition();
                    stateGeneratorDef.setBeanClass(SpelExpressionRetryStateGenerator.class);
                    stateGeneratorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, "headers['retryId'].toString()");
                    retryAdvicePV.add("retryStateGenerator", stateGeneratorDef);

                    String failedSqlId = asynRunner.failedSqlId();
                    if (StringUtils.isEmpty(failedSqlId)) {
                        GenericBeanDefinition nullRecoveryCallbackDef = new GenericBeanDefinition();
                        nullRecoveryCallbackDef.setBeanClass(NullRecoveryCallback.class);
                        retryAdvicePV.add("recoveryCallback", nullRecoveryCallbackDef);
                    } else {
                        GenericBeanDefinition mybatisUpdateRecoveryCallbackDef = new GenericBeanDefinition();
                        mybatisUpdateRecoveryCallbackDef.setBeanClass(MybatisUpdateRecoveryCallback.class);
                        mybatisUpdateRecoveryCallbackDef.getPropertyValues().add("sql", failedSqlId);
                        retryAdvicePV.add("recoveryCallback", mybatisUpdateRecoveryCallbackDef);
                    }

                    //service activator
                    String serviceActivatorId = baseName + "_serviceActivator";
                    RootBeanDefinition serviceActivatorDef = new RootBeanDefinition(ServiceActivatorFactoryBean.class);
                    MutablePropertyValues serviceActivatorPV = serviceActivatorDef.getPropertyValues();
                    serviceActivatorPV.add("targetObject", new RuntimeBeanReference(s));
                    serviceActivatorPV.add("targetMethodName", methodName);
                    ManagedList servcieActivatorChain = new ManagedList();
                    servcieActivatorChain.add(retryAdviceDef);
                    serviceActivatorPV.add("adviceChain", servcieActivatorChain);

                    //transactionInterceptor
                    String tsInterceptorId = baseName + "_transactionInterceptor";
                    RootBeanDefinition transactionInterceptorDef = new RootBeanDefinition(TransactionInterceptor.class);
                    MutablePropertyValues transactionInterceptorPV = transactionInterceptorDef.getPropertyValues();
                    transactionInterceptorPV.add("transactionManager", new RuntimeBeanReference("transactionManager"));
                    GenericBeanDefinition attributeSourceDef = new GenericBeanDefinition();
                    attributeSourceDef.setBeanClass(MatchAlwaysTransactionAttributeSource.class);
                    GenericBeanDefinition attributeDef = new GenericBeanDefinition();
                    attributeDef.setBeanClass(DefaultTransactionAttribute.class);
                    MutablePropertyValues attributePV = attributeDef.getPropertyValues();
                    attributePV.add("propagationBehaviorName", "PROPAGATION_REQUIRED");
                    attributePV.add("isolationLevelName", "ISOLATION_DEFAULT");
                    attributePV.add("timeout", "-1");
                    attributePV.add("readOnly", "false");
                    attributeSourceDef.getPropertyValues().add("transactionAttribute",attributeDef);
                    transactionInterceptorPV.add("transactionAttributeSource", attributeSourceDef);

                    //transactionSynchronizationFactory
                    String tranSynFacId = baseName + "_transactionSynchronizationFactory";
                    GenericBeanDefinition processorDef = new GenericBeanDefinition();
                    processorDef.setBeanClass(ExpressionEvaluatingTransactionSynchronizationProcessor.class);
                    processorDef.getPropertyValues().add("afterRollbackChannel", new RuntimeBeanReference(delayChannelId));
                    RootBeanDefinition tranSynFacDef = new RootBeanDefinition(DefaultTransactionSynchronizationFactory.class);
                    tranSynFacDef.getConstructorArgumentValues().addIndexedArgumentValue(0,processorDef);

                    //poller
                    //TODO 线程池
                    String pollingConsumerId = baseName + "_poller";
                    RootBeanDefinition pollingConsumerDef = new RootBeanDefinition(QuartzPollingConsumer.class);
                    pollingConsumerDef.setLazyInit(true);
                    ConstructorArgumentValues pollingConsumerArgs = pollingConsumerDef.getConstructorArgumentValues();
                    pollingConsumerArgs.addIndexedArgumentValue(0,new RuntimeBeanReference(channelId));
                    pollingConsumerArgs.addIndexedArgumentValue(1,new RuntimeBeanReference(serviceActivatorId));
                    MutablePropertyValues pollingConsumerPV = pollingConsumerDef.getPropertyValues();
                    pollingConsumerPV.add("cronExpress", asynRunner.cron());
                    ManagedList managedList = new ManagedList();
                    managedList.add(new RuntimeBeanReference(tsInterceptorId));
                    pollingConsumerPV.add("adviceChain", managedList);
                    pollingConsumerPV.add("maxMessagesPerPoll", asynRunner.maxPerPoll());
                    pollingConsumerPV.add("transactionSynchronizationFactory", new RuntimeBeanReference(tranSynFacId));

                    //delayer
                    String delayerId = baseName + "_delayer";
                    RootBeanDefinition delayerDef = new RootBeanDefinition(DelayHandler.class);
                    delayerDef.getConstructorArgumentValues().addIndexedArgumentValue(0,delayerId);
                    MutablePropertyValues delayerPV = delayerDef.getPropertyValues();
                    //TODO 不能写死
                    delayerPV.add("messageStore", new RuntimeBeanReference("redisMessageStore"));
                    delayerPV.add("defaultDelay", asynRunner.timeUnit().toMillis(asynRunner.delayedTime()));
                    delayerPV.add("outputChannelName", channelId);

                    //delayerPoller
                    String delayPollerId = baseName + "_delayPoller";
                    RootBeanDefinition delayPollerDef = new RootBeanDefinition(QuartzPollingConsumer.class);
                    delayChannelDef.setLazyInit(true);
                    ConstructorArgumentValues delayPoolerArgs = delayPollerDef.getConstructorArgumentValues();
                    delayPoolerArgs.addIndexedArgumentValue(0,new RuntimeBeanReference(delayChannelId));
                    delayPoolerArgs.addIndexedArgumentValue(1,new RuntimeBeanReference(delayerId));
                    MutablePropertyValues delayPollerPV = delayPollerDef.getPropertyValues();
                    delayPollerPV.add("cronExpress", asynRunner.retryCron());


                    registry.registerBeanDefinition(channelQueueId, messageGroupQueueDef);
                    registry.registerBeanDefinition(channelId, channelDef);
                    registry.registerBeanDefinition(delayChannelQueueId,delayChannelQueueDef);
                    registry.registerBeanDefinition(delayChannelId,delayChannelDef);
                    registry.registerBeanDefinition(serviceActivatorId,serviceActivatorDef);
                    registry.registerBeanDefinition(tsInterceptorId,transactionInterceptorDef);
                    registry.registerBeanDefinition(tranSynFacId,tranSynFacDef);
                    registry.registerBeanDefinition(pollingConsumerId,pollingConsumerDef);
                    registry.registerBeanDefinition(delayerId, delayerDef);
                    registry.registerBeanDefinition(delayPollerId,delayPollerDef);
                });
            } catch (ClassNotFoundException ignored) {
            }
        }
    }

    private Class<?> determineInterface(Class<?> orgin, String beanName) {
        for (Class<?> anInterface : orgin.getInterfaces()) {
            String simpleName = anInterface.getSimpleName();
            if (simpleName.toUpperCase().equals(beanName.toUpperCase())) {
                return anInterface;
            } else {
                String canonicalName = orgin.getCanonicalName();
                String interfaceCanonicalName = anInterface.getCanonicalName();
                String basePackage = interfaceCanonicalName.substring(0, interfaceCanonicalName.lastIndexOf("."));
                if (canonicalName.startsWith(basePackage)) {
                    return anInterface;
                }
            }
        }
        return null;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
