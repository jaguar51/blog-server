package me.academeg.blog.dal.utils;

import org.hibernate.Hibernate;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 * @date 13.02.2017
 */
final public class Relations {

    public static <One, Many> One setOneToMany(
        One oneSide,
        Collection<Many> collection,
        Collection<Many> manySide,
        Function<Many, One> getter,
        BiConsumer<Many, One> setter
    ) {
        if (collection == manySide) {
            return oneSide;
        }

        if (manySide != null) {
            manySide.forEach(item -> removeOneToMany(oneSide, collection, item, setter));
        }

        collection.clear();

        if (manySide != null) {
            manySide.forEach(item -> addOneToMany(oneSide, collection, item, getter, setter));
        }

        return oneSide;
    }

    public static <One, Many> One addOneToMany(
        One oneSide,
        Collection<Many> collection,
        Many oneOfMany,
        Function<Many, One> getter,
        BiConsumer<Many, One> setter
    ) {
        if (collection.contains(oneOfMany)) {
            return oneSide;
        }

        collection.add(oneOfMany);

        if (getter.apply(oneOfMany) != oneSide) {
            setter.accept(oneOfMany, oneSide);
        }

        return oneSide;
    }

    public static <One, Many> One removeOneToMany(
        One oneSide,
        Collection<Many> collection,
        Many oneOfMany,
        BiConsumer<Many, One> setter
    ) {
        collection.remove(oneOfMany);
        setter.accept(oneOfMany, null);
        return oneSide;
    }

    public static <Many> Collection<Many> getOneToMany(Collection<Many> realCollection) {
        if (realCollection == null) {
            return null;
        }

        if (Hibernate.isInitialized(realCollection)) {
            return Collections.unmodifiableCollection(realCollection);
        }

        return realCollection;
    }

    public static <One, Many> Many setManyToOne(
        Many self,
        One oneSide,
        Function<Many, One> getter,
        Consumer<One> realSetter,
        BiConsumer<One, Many> addMethod,
        BiConsumer<One, Many> removeMethod
    ) {
        One oldOneSide = getter.apply(self);

        if (oldOneSide == oneSide) {
            return self;
        }

        realSetter.accept(oneSide);

        if (oldOneSide != null) {
            removeMethod.accept(oldOneSide, self);
        }

        if (oneSide != null) {
            addMethod.accept(oneSide, self);
        }

        return self;
    }

    public static <Left, Right> Left addManyToMany(
        Left left,
        Right right,
        BiFunction<Left, Right, Boolean> leftHasRight,
        Consumer<Right> leftAdder,
        BiConsumer<Right, Left> rightAdder
    ) {
        if (leftHasRight.apply(left, right)) {
            return left;
        }
        leftAdder.accept(right);
        rightAdder.accept(right, left);
        return left;
    }

    public static <Left, Right> Left removeManyToMany(
        Left left,
        Right right,
        BiFunction<Left, Right, Boolean> leftHasRight,
        Consumer<Right> leftRemover,
        BiConsumer<Right, Left> rightRemover
    ) {
        if (leftHasRight.apply(left, right)) {
            leftRemover.accept(right);
            rightRemover.accept(right, left);
        }
        return left;
    }

    public static <T> void setManyToMany(
        Collection<T> source,
        Collection<T> destination,
        BiConsumer<Collection<T>, T> remover,
        Consumer<T> adder
    ) {
        destination.forEach(el -> remover.accept(source, el));
        destination.clear();

        if (source != null) {
            source.forEach(adder);
        }
    }
}