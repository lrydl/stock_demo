package com.smallrig.mall.template;

import net.bytebuddy.matcher.ElementMatcher;

public class AgeMatcher implements ElementMatcher<UserContext> {

    @Override
    public boolean matches(UserContext userContext) {
        if(userContext.getAge()>18){
            return true;
        }
        return false;
    }
}
