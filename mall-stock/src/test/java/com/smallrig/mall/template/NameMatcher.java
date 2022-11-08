package com.smallrig.mall.template;

import net.bytebuddy.matcher.ElementMatcher;
import org.apache.commons.lang3.StringUtils;

public class NameMatcher implements ElementMatcher<UserContext> {

    @Override
    public boolean matches(UserContext userContext) {
        if(StringUtils.isNotEmpty(userContext.getName())){
            return true;
        }
        return false;
    }
}
