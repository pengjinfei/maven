package com.pengjinfei.maven.configuration.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.gateway.GatewayMethodMetadata;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.integration.store.MessageGroupQueue;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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
                        managedList.add(new RuntimeBeanReference("retryIdChannelInterceptor"));
                        channelDef.getPropertyValues().add("interceptors",managedList);
                    }


                    //gateway
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

                    registry.registerBeanDefinition(channelQueueId, messageGroupQueueDef);
                    registry.registerBeanDefinition(channelId, channelDef);
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
