package antessio.eventsourcing.jsonconversion;

public interface JsonConverter {

    <T> T fromJson(String json, Class<? extends T> cls);

    <T> String toJson(T obj);

}
