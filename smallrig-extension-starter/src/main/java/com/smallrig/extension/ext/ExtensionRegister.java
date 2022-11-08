package com.smallrig.extension.ext;

import com.smallrig.extension.ExtensionCoordinate;
import com.smallrig.extension.ExtensionException;
import com.smallrig.extension.ExtensionPointI;
import com.smallrig.extension.anno.Extension;
import com.smallrig.extension.dto.BizScenario;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;

@Component
public class ExtensionRegister {

    /**
     * 扩展点接口名称不合法
     */
    private static final String EXTENSION_INTERFACE_NAME_ILLEGAL = "extension_interface_name_illegal";
    /**
     * 扩展点不合法
     */
    private static final String EXTENSION_ILLEGAL = "extension_illegal";
    /**
     * 扩展点定义重复
     */
    private static final String EXTENSION_DEFINE_DUPLICATE = "extension_define_duplicate";

    @Resource
    private ExtensionRepository extensionRepository;

    public final static String EXTENSION_EXTPT_NAMING = "ExtPt";


    public void doRegistration(ExtensionPointI extensionObject) {
        Class<?> extensionClz = extensionObject.getClass();
        if (AopUtils.isAopProxy(extensionObject)) {
            extensionClz = ClassUtils.getUserClass(extensionObject);
        }
        Extension extensionAnn = AnnotationUtils.findAnnotation(extensionClz, Extension.class);
        BizScenario bizScenario = BizScenario.valueOf(extensionAnn.bizId(), extensionAnn.useCase(), extensionAnn.scenario());
        ExtensionCoordinate extensionCoordinate = new ExtensionCoordinate(calculateExtensionPoint(extensionClz), bizScenario.getUniqueIdentity());
        ExtensionPointI preVal = extensionRepository.getExtensionRepo().put(extensionCoordinate, extensionObject);
        if (preVal != null) {
            String errMessage = "Duplicate registration is not allowed for :" + extensionCoordinate;
            throw new ExtensionException(EXTENSION_DEFINE_DUPLICATE, errMessage);
        }
    }

    /**
     * @param targetClz
     * @return
     */
    private String calculateExtensionPoint(Class<?> targetClz) {
        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(targetClz);
        if (interfaces == null || interfaces.length == 0) {
            throw new ExtensionException(EXTENSION_ILLEGAL, "Please assign a extension point interface for " + targetClz);
        }
        for (Class intf : interfaces) {
            String extensionPoint = intf.getSimpleName();
            if (extensionPoint.contains(EXTENSION_EXTPT_NAMING)) {
                return intf.getName();
            }
        }
        String errMessage = "Your name of ExtensionPoint for " + targetClz +
                " is not valid, must be end of " + EXTENSION_EXTPT_NAMING;
        throw new ExtensionException(EXTENSION_INTERFACE_NAME_ILLEGAL, errMessage);
    }

}
