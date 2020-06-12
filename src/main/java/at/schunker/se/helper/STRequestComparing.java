package at.schunker.se.helper;

import at.schunker.se.model.STHttpRequest;

import java.util.*;

public class STRequestComparing {

    public static boolean compareRequests(STHttpRequest req1, STHttpRequest req2) {
        List<Object> payloads = new ArrayList<>();
        payloads.add(req1.getBody());
        payloads.add(req2.getBody());

        boolean isEqualType = STRequestComparing.compareObjectTypesRec(payloads.toArray());
        boolean isEqual = STRequestComparing.compareObjectsRec(payloads.toArray());

        if (!isEqualType || !isEqual) {
            return false;
        }

        return Objects.equals(req1.getUri(), req2.getUri()) &&
                Objects.equals(req1.getQueryParameters(), req2.getQueryParameters());
    }

    public static int compareObjectTypes(Object[] objects) {
        System.err.println("compareObjectTypes");
        if (objects == null || objects.length < 2) {
            return -1;
        }

        Object object = objects[0];
        String type = object.getClass().getSimpleName();

        for (Object obj : objects) {
            String objType = obj.getClass().getSimpleName();
            System.err.println(objType);
            if (!objType.equals(type)) {
                return 0;
            }
        }

        return 1;
    }

    public static int compareObjects(Object[] objects) {
        if (objects == null || objects.length < 2) {
            return -1;
        }

        Object object = objects[0];
        for (Object obj : objects) {
            if (!obj.equals(object)) {
                return 0;
            }
        }

        return 1;
    }

    public static boolean compareObjectTypesRec(Object[] objects) {
        int comparison = STRequestComparing.compareObjectTypes(objects);
        if (comparison == -1 || comparison == 0) {
            return false;
        }

        Class jClass = objects[0].getClass();
        String type = jClass.getSimpleName();

        if (Map.class.isAssignableFrom(jClass)) {
            List<Object> objectList = new ArrayList();

            for (Object obj : objects) {
                Map<String, Object> map = (Map<String, Object>) obj;
                for (Object o : map.values()) {
                    objectList.add(o);
                }
            }

            return compareObjectTypesRec(objectList.toArray());
        }

        if (List.class.isAssignableFrom(jClass)) {
            List<Object> objectList = new ArrayList();

            for (Object obj : objects) {
                List<Object> list = (List<Object>) obj;
                objectList.addAll(list);
            }

            return compareObjectTypesRec(objectList.toArray());
        }

        if (type.equals(HashMap.class.getSimpleName())) {
            List<Object> objectList = new ArrayList();

            for (Object obj : objects) {
                Map<String, Object> map = (Map<String, Object>) obj;
                for (Object o : map.values()) {
                    objectList.add(o);
                }
            }

            return compareObjectTypesRec(objectList.toArray());
        }

        if (type.equals(ArrayList.class.getSimpleName())) {
            List<Object> objectList = new ArrayList();

            for (Object obj : objects) {
                List<Object> list = (List<Object>) obj;
                objectList.addAll(list);
            }

            return compareObjectTypesRec(objectList.toArray());
        }

        return true;
    }

    public static boolean compareObjectsRec(Object[] objects) {
        int comparison = STRequestComparing.compareObjectTypes(objects);
        if (comparison == -1 || comparison == 0) {
            return false;
        }

        Class jClass = objects[0].getClass();
        String type = jClass.getSimpleName();

        if (Map.class.isAssignableFrom(jClass)) {
            List<Object> objectList = new ArrayList();

            for(Object object : objects) {
                Map<String, Object> obj = (Map<String, Object>) object;
                Set<String> attributes = obj.keySet();
                objectList.addAll(attributes);
            }

            return compareObjectsRec(objectList.toArray());
        }

        if (List.class.isAssignableFrom(jClass)) {
            List<Object> objectList = new ArrayList();

            for (Object object : objects) {
                List<Object> list = (List<Object>) object;
                objectList.addAll(list);
            }

            return compareObjectsRec(objectList.toArray());
        }

        if (type.equals(HashMap.class.getSimpleName())) {
            List<Object> objectList = new ArrayList();

            for(Object object : objects) {
                Map<String, Object> obj = (Map<String, Object>) object;
                Set<String> attributes = obj.keySet();
                objectList.addAll(attributes);
            }

            return compareObjectsRec(objectList.toArray());
        }

        if (type.equals(ArrayList.class.getSimpleName())) {
            List<Object> objectList = new ArrayList();

            for (Object object : objects) {
                List<Object> list = (List<Object>) object;
                objectList.addAll(list);
            }

            return compareObjectsRec(objectList.toArray());
        }

        comparison = STRequestComparing.compareObjects(objects);
        if (comparison == 0) {
            return false;
        }

        return true;
    }

}
