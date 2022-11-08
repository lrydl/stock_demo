package com.smallrig.mall.template.extension;

import com.smallrig.extension.anno.Extension;
import org.springframework.stereotype.Service;

@Service
@Extension(bizId = "v1")
public class ExtV1ExtPt implements ExtVersionExtPt {


    @Override
    public void sayHello() {
        System.out.println("hello v1");
    }
}
