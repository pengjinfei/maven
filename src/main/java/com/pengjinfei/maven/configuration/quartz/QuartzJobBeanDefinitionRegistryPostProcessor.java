package com.pengjinfei.maven.configuration.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Component
@Slf4j
public class QuartzJobBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
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
                    QuartzJob quartzJob = AnnotationUtils.getAnnotation(method, QuartzJob.class);
                    if (quartzJob == null) {
                        return;
                    }
                    String methodName = method.getName();
                    int parameterCount = method.getParameterCount();
                    if (parameterCount != 0) {
                        log.warn("find @QuartzJob on class {} method {}, but parameter is not null.",
                                aClass.getName(),
                                methodName);
                    }
                    RootBeanDefinition jobDetailBeanDef = new RootBeanDefinition();
                    jobDetailBeanDef.setBeanClass(JobDetailFactoryBean.class);
                    MutablePropertyValues jobDetailPV = jobDetailBeanDef.getPropertyValues();
                    jobDetailPV.add("beanName", s);
                    jobDetailPV.add("methodName", methodName);

                    String quartzJobName = quartzJob.name();
                    //job 的 name 可能为中文
                    String jobName = quartzJobName.equals("") ? s + "_" + methodName + "_job" : quartzJobName;
                    jobDetailPV.add("name", jobName);
                    jobDetailPV.add("group", quartzJob.group());
                    jobDetailPV.add("concurrent", quartzJob.concurrent());
                    registry.registerBeanDefinition(jobName, jobDetailBeanDef);

                    RootBeanDefinition triggerBeanDef = new RootBeanDefinition();
                    //trigger 的 name 一定要为英文
                    String triggerName = s + "_" + methodName + "_trigger";
                    RuntimeBeanReference jobDetailRef = new RuntimeBeanReference(jobName);
                    triggerBeanDef.setBeanClass(CronTriggerFactoryBean.class);
                    MutablePropertyValues triggerPV = triggerBeanDef.getPropertyValues();
                    triggerPV.add("cronExpression", quartzJob.cronExpression());
                    triggerPV.addPropertyValue("jobDetail", jobDetailRef);
                    triggerPV.add("name", triggerName);
                    triggerPV.add("group", quartzJob.group());
                    triggerPV.addPropertyValue("misfireInstruction", quartzJob.misfireInstruction());
                    registry.registerBeanDefinition(triggerName, triggerBeanDef);
                });
            } catch (ClassNotFoundException ignored) {
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
