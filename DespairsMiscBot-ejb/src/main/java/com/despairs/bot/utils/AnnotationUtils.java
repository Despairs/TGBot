/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.utils;

import java.lang.annotation.Annotation;

/**
 *
 * @author EKovtunenko
 */
public class AnnotationUtils {

    public static Annotation findAnnotation(Class source, Class annotationClass) {
        Annotation annotation = source.getAnnotation(annotationClass);
        if (annotation == null) {
            if (source.getSuperclass() != null) {
                annotation = findAnnotation(source.getSuperclass(), annotationClass);
            }
        }
        return annotation;
    }
}