package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.History.Event;

public class ReferencedObjects {
    Map<String, List<Event>> maps = new HashMap<String, List<Event>>();

    public void add(Event event) {
        List<Event> map = maps.get(event.type);
        if (map == null) {
            map = new ArrayList<Event>();
            maps.put(event.type, map);
        }
        map.add(event);
    }
    
    public String[] getNames(String shortIdentifier) {
        List<String> list = new ArrayList<String>();
        List<Event> instances = maps.get(shortIdentifier);
        if (instances != null) {
            for (Event event : instances) {
                list.add(event.name);
            }
        }
        return list.toArray(new String[0]);
    }

    public String[] getReferences(String shortIdentifier) {
        List<String> list = new ArrayList<String>();
        List<Event> instances = maps.get(shortIdentifier);
        if (instances != null) {
            for (Event event : instances) {
                list.add(event.id);
            }
        }
        return list.toArray(new String[0]);
    }

}
