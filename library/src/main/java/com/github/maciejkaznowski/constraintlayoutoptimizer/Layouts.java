package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Layouts implements Parcelable {

    public static final Parcelable.Creator<Layouts> CREATOR = new Parcelable.Creator<Layouts>() {
        @Override
        public Layouts createFromParcel(Parcel source) {
            return new Layouts(source);
        }

        @Override
        public Layouts[] newArray(int size) {
            return new Layouts[size];
        }
    };
    private boolean includeAllLayouts;
    //TODO This will cause autoboxing, but it doesn't matter too much since included/excluded layouts won't usually be big anyway
    private Set<Integer> includedLayouts;
    private Set<Integer> excludedLayouts;

    public Layouts() {
        this.includeAllLayouts = false;
        this.includedLayouts = new ArraySet<>();
        this.excludedLayouts = new ArraySet<>();
    }

    protected Layouts(Parcel in) {
        this.includeAllLayouts = in.readByte() != 0;

        List<Integer> includedLayoutsList = new ArrayList<>();
        in.readList(includedLayoutsList, ArrayList.class.getClassLoader());
        this.includedLayouts = new ArraySet<>(includedLayoutsList);

        List<Integer> excludedLayoutsList = new ArrayList<>();
        in.readList(excludedLayoutsList, ArrayList.class.getClassLoader());
        this.excludedLayouts = new ArraySet<>(excludedLayoutsList);
    }

    @NonNull
    public Layouts includeAllLayouts(boolean allLayouts) {
        this.includeAllLayouts = allLayouts;
        return this;
    }

    @NonNull
    public Layouts includeLayout(@LayoutRes int layoutRes) {
        this.includedLayouts.add(layoutRes);
        this.excludedLayouts.remove(layoutRes);
        return this;
    }

    @NonNull
    public Layouts excludeLayout(@LayoutRes int layoutRes) {
        this.excludedLayouts.add(layoutRes);
        this.includedLayouts.remove(layoutRes);
        return this;
    }

    @NonNull
    List<Layout> find(@NonNull Context context) throws RClassNotFoundException, LayoutClassNotFoundException, InvocationTargetException, CouldNotInstantiateLayoutConstructor, InstantiationException, IllegalAccessException, LayoutNotFoundException { //TODO remove throws exception
        //the R.layout class
        Class layoutResourceClass = getLayoutClass(context);
        Field[] layoutFields = layoutResourceClass.getDeclaredFields();
        Object layoutClassInstance = getLayoutClassInstance(layoutResourceClass);

        if (includeAllLayouts) {
            //add all layouts which aren't in the excluded list
            return getAllLayouts(layoutClassInstance, layoutFields, excludedLayouts);
        } else {
            //add layouts which are in the include list
            List<Layout> layouts = new ArrayList<>(includedLayouts.size());
            for (int layoutRes : includedLayouts) {
                String layoutName = resolveLayoutName(layoutClassInstance, layoutFields, layoutRes);
                Layout layout = new Layout(layoutName, layoutRes);
                layouts.add(layout);
            }

            return layouts;
        }
    }

    @NonNull
    private Object getLayoutClassInstance(@NonNull Class layoutClass) throws CouldNotInstantiateLayoutConstructor, IllegalAccessException, InvocationTargetException, InstantiationException {
        //constructor is private with no args
        Constructor constructor;
        try {
            constructor = layoutClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new CouldNotInstantiateLayoutConstructor(e);
        }
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    @NonNull
    private String resolveLayoutName(@NonNull Object layoutClassInstance,
                                     @NonNull Field[] layoutFields,
                                     @LayoutRes int layoutRes) throws LayoutNotFoundException, IllegalAccessException {
        for (Field field : layoutFields) {
            if (field.getInt(layoutClassInstance) == layoutRes) {
                return field.getName();
            }
        }

        throw new LayoutNotFoundException(layoutRes);
    }

    @NonNull
    private Class getLayoutClass(@NonNull Context context) throws RClassNotFoundException, LayoutClassNotFoundException {
        Class R = getRClass(context);
        Class[] innerClasses = R.getDeclaredClasses();
        for (Class innerClass : innerClasses) {
            if (innerClass.getSimpleName().equals("layout")) {
                return innerClass;
            }
        }

        throw new LayoutClassNotFoundException();
    }

    @NonNull
    private Class getRClass(@NonNull Context context) throws RClassNotFoundException {
        //returns the "R" resources class, e.g. com.company.app.R.class
        String appPackageName = context.getPackageName(); //might not be where the R class is, e.g. could be com.company.app.appended_package_name
        String RPackageName = appPackageName;

        try {
            return Class.forName(RPackageName + ".R");
        } catch (ClassNotFoundException e) {
            throw new RClassNotFoundException(e);
        }
    }

    @NonNull
    private List<Layout> getAllLayouts(@NonNull Object layoutClassInstance,
                                       @NonNull Field[] layoutFields,
                                       @NonNull Set<Integer> excludedLayouts) throws IllegalAccessException {
        //map each field to a LayoutResource
        List<Layout> layouts = new ArrayList<>();
        for (int i = 0; i < layoutFields.length; i++) {
            Field field = layoutFields[i];

            int value = field.getInt(layoutClassInstance);
            if (excludedLayouts.contains(value)) break; //ignore if in excluded list
            String name = field.getName();

            Layout layout = new Layout(name, value);
            layouts.add(layout);
        }

        return layouts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.includeAllLayouts ? (byte) 1 : (byte) 0);
        dest.writeList(new ArrayList<>(this.includedLayouts));
        dest.writeList(new ArrayList<>(this.excludedLayouts));
    }

    private static class RClassNotFoundException extends ClassNotFoundException {


        private RClassNotFoundException(@NonNull ClassNotFoundException e) {
            super("Could not find R class", e);
        }
    }

    private static class LayoutClassNotFoundException extends ClassNotFoundException {

        private LayoutClassNotFoundException() {
            super("Could not find a layout class in your R class");
        }
    }

    private static class LayoutNotFoundException extends Exception {

        private LayoutNotFoundException(@LayoutRes int layoutRes) {
            super("Could not find layout in R.layout with value " + Utils.getLayoutHexString(layoutRes));
        }
    }

    private static class CouldNotInstantiateLayoutConstructor extends Exception {

        CouldNotInstantiateLayoutConstructor(@NonNull NoSuchMethodException e) {
            super("Could not instantiate layout constructor", e);
        }
    }
}
