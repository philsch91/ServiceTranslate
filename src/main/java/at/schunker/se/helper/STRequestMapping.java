package at.schunker.se.helper;

import at.schunker.se.model.STHttpRequest;

import java.util.*;

public class STRequestMapping {

    public static boolean compareRequests(STHttpRequest req1, STHttpRequest req2) {
        List<STHttpRequest> requests = new ArrayList<>();
        requests.add(req1);
        requests.add(req2);

        boolean isEqual1 = STRequestMapping.compareObjectsRec(requests.toArray());
        boolean isEqual2 = STRequestMapping.compareObjectAttributesRec(requests.toArray());

        return isEqual1 && isEqual2;
        /*
        return Objects.equals(req1.getUri(), req2.getUri()) &&
                Objects.equals(req1.getQueryParameters(), req2.getQueryParameters());
         */
    }

    public static int compareObjects(Object[] objects) {
        if (objects == null || objects.length < 2) {
            return -1;
        }

        Object object = objects[0];
        String type = object.getClass().getSimpleName();

        for(Object obj : objects) {
            String objType = obj.getClass().getSimpleName();
            System.err.println(objType);
            if (!objType.equals(type)) {
                return 0;
            }
        }

        return 1;
    }

    public static boolean compareObjectsRec(Object[] objects) {
        int comparison = STRequestMapping.compareObjects(objects);
        if (comparison == -1 || comparison == 0) {
            return false;
        }

        String type = objects[0].getClass().getSimpleName();

        if (type.equals(HashMap.class.getSimpleName())) {
            List<Object> objectList = new ArrayList();

            for (Object obj : objects) {
                Map<String, Object> map = (Map<String, Object>) obj;
                for (Object o : map.values()) {
                    objectList.add(o);
                }
            }

            return compareObjectsRec(objectList.toArray());
        }

        if (type.equals(ArrayList.class.getSimpleName())) {
            List<Object> objectList = new ArrayList();

            for (Object obj : objects) {
                List<Object> list = (List<Object>) obj;
                objectList.addAll(list);
            }

            return compareObjectsRec(objectList.toArray());
        }

        return true;
    }

    public static boolean compareObjectAttributesRec(Object[] objects) {
        int comparison = STRequestMapping.compareObjects(objects);
        if (comparison == -1 || comparison == 0) {
            return false;
        }

        String type = objects[0].getClass().getSimpleName();

        if (type.equals(HashMap.class.getSimpleName())) {
            List<Object> objectList = new ArrayList();

            for(Object object : objects) {
                Map<String, Object> obj = (Map<String, Object>) object;
                Set<String> attributes = obj.keySet();
                objectList.addAll(attributes);
            }

            return compareObjectAttributesRec(objectList.toArray());
        }

        if (type.equals(ArrayList.class.getSimpleName())) {
            List<Object> objectList = new ArrayList();

            for (Object object : objects) {
                List<Object> obj = (List<Object>) object;
                for (Object o : obj) {
                    objectList.add(o);
                }
            }

            return compareObjectAttributesRec(objectList.toArray());
        }

        return true;
    }

}
