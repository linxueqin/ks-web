package ks.web.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Path {

    public String name() default "";
    
    public String[] parameters() default {};
    
    public String permission() default "";
    
    public String description() default "";
}
