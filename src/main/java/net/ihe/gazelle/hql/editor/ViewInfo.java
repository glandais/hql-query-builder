package net.ihe.gazelle.hql.editor;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE, FIELD })
@Retention(RUNTIME)
public @interface ViewInfo {

	String label();

	int rank();

	boolean table() default true;

	boolean details() default true;

	boolean editor() default true;

}
