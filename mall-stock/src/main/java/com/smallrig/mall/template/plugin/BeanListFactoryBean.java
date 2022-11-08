package com.smallrig.mall.template.plugin;

import lombok.NonNull;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.plugin.core.support.AbstractTypeAwareSupport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class BeanListFactoryBean<T> extends AbstractTypeAwareSupport<T> implements FactoryBean<List<T>> {
    private static final Comparator<Object> COMPARATOR = new AnnotationAwareOrderComparator();

    public BeanListFactoryBean() {
        setType((Class<T>) Vehicle.class);
    }

    @Override
    @NonNull
    public List<T> getObject() {
        List<T> beans = new ArrayList();
        beans.addAll(this.getBeans());
        Collections.sort(beans, COMPARATOR);
        return beans;
    }

    @Override
    @NonNull
    public Class<?> getObjectType() {
        return List.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
