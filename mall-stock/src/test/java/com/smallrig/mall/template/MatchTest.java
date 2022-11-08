package com.smallrig.mall.template;

import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

//策略，责任链，builder，观察者
public class MatchTest {


    @Test
    public void test(){
        UserContext us = new UserContext(2,"321");

        boolean matches1 = ElementMatchers.anyOf(1, 2, 3).matches(1);
        System.out.println(matches1);

        boolean matches = ElementMatchers.any().and(new AgeMatcher()).and(new NameMatcher()).matches(us);

        System.out.println(matches);


        System.out.println(ElementMatchers.is(1).matches(1));

    }

}
