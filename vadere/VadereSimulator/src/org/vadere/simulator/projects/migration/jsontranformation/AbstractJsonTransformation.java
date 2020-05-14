package org.vadere.simulator.projects.migration.jsontranformation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.vadere.simulator.entrypoints.Version;
import org.vadere.simulator.projects.migration.MigrationException;
import org.vadere.state.util.StateJsonConverter;
import org.vadere.util.logging.Logger;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public abstract class AbstractJsonTransformation implements JsonTransformation, JsonNodeExplorer {
    protected final static Logger logger = Logger.getLogger(AbstractJsonTransformation.class);

    LinkedList<JsonTransformationHook> postHooks;
    LinkedList<JsonTransformationHook> preHooks;

    public AbstractJsonTransformation() {
        this.postHooks = new LinkedList<>();
        this.preHooks = new LinkedList<>();
        initDefaultHooks();
    }

    public static JsonTransformation get(Version currentVersion) throws MigrationException {
        if (currentVersion.equalOrSmaller(Version.UNDEFINED))
            throw new MigrationException("There is now Transformation for Version " + Version.UNDEFINED.toString());

        if (currentVersion.equals(Version.latest()))
            throw new MigrationException("No Transformation needed. Already latest Version!");


        org.vadere.simulator.projects.migration.jsontranformation.JsonTransformationFactory factory
                = org.vadere.simulator.projects.migration.jsontranformation.JsonTransformationFactory.instance();
        JsonTransformation ret;
        try {
            ret = factory.getInstanceOf(currentVersion.nextVersion().label('_'));
        } catch (ClassNotFoundException e) {
            throw new MigrationException("Cannot find Transformation in Factory for Version " + currentVersion.nextVersion().label());
        }
        return ret;
    }

    @Override
    public void addPreHookFirst(JsonTransformationHook hook) {
        preHooks.addFirst(hook);
    }

    @Override
    public void addPreHookLast(JsonTransformationHook hook) {
        preHooks.addLast(hook);
    }

    @Override
    public void addPostHookFirst(JsonTransformationHook hook) {
        postHooks.addFirst(hook);
    }

    @Override
    public void addPostHookLast(JsonTransformationHook hook) {
        postHooks.addLast(hook);
    }


    @Override
    public JsonNode applyPreHooks(JsonNode node) throws MigrationException {
        JsonNode ret = node;
        for (JsonTransformationHook hook : preHooks) {
            ret = hook.applyHook(ret);
        }
        return ret;
    }

    @Override
    public JsonNode applyPostHooks(JsonNode node) throws MigrationException {
        JsonNode ret = node;
        for (JsonTransformationHook hook : postHooks) {
            ret = hook.applyHook(ret);
        }
        return ret;
    }

    @Override
    abstract public JsonNode applyTransformation(JsonNode node) throws MigrationException;

    protected abstract void initDefaultHooks();

    abstract public Version getTargetVersion();

    /**
     * Create a Copy of Json and put nodes in user specified order
     *
     * @param target        new LinkedHashMap with wherer to put nodes
     * @param source        source
     * @param key           key to add to new HashMap
     * @param children      Specify Order on second level
     */
    public static void putObject(LinkedHashMap<Object, Object> target,
                          LinkedHashMap<Object, Object> source,
                          String key, String... children){

        Object obj = source.get(key);
        if (obj != null) {
            if (children.length > 0) {
                LinkedHashMap<Object, Object> node = new LinkedHashMap<>();
                LinkedHashMap<Object, Object> parent = (LinkedHashMap<Object, Object>) obj;
                for (String childKey : children) {
                    Object childObj = parent.get(childKey);
                    if (childObj != null) {
                        node.put(childKey, childObj);
                    }
                }
                obj = node;
            }
            target.put(key, obj);
        }
    }

    public JsonNode addCommitHashWarningIfMissing(JsonNode node){
        JsonNode commitHash = path(node, "commithash");
        if (commitHash.isMissingNode()){
            ((ObjectNode)node).put("commithash", "warning: no commit hash");
        }
        return node;
    }

    // choose  sort order based on targetVersion.
    public JsonNode sort (JsonNode node) {

        if (getTargetVersion().equalOrBigger(Version.V0_8)){
            node = sort_since_V08(node);
        } else {
            node = sort_since_V01(node);
        }

        return  node;
    }

    private JsonNode sort_since_V08(JsonNode node) {
        LinkedHashMap source = (LinkedHashMap) StateJsonConverter.convertJsonNodeToObject(node);
        LinkedHashMap<Object, Object> sortedRoot = new LinkedHashMap<>();
        putObject(sortedRoot, source, "name");
        putObject(sortedRoot, source, "description");
        putObject(sortedRoot, source, "release");
        putObject(sortedRoot, source, "commithash");
        putObject(sortedRoot, source, "processWriters","files", "processors", "isTimestamped", "isWriteMetaData");
        putObject(sortedRoot, source, "scenario", "mainModel", "attributesModel", "attributesSimulation", "topography", "eventInfos");

        return  StateJsonConverter.deserializeToNode(sortedRoot);
    }

    private JsonNode sort_since_V01(JsonNode node) {
        LinkedHashMap source = (LinkedHashMap) StateJsonConverter.convertJsonNodeToObject(node);
        LinkedHashMap<Object, Object> sortedRoot = new LinkedHashMap<>();
        putObject(sortedRoot, source, "name");
        putObject(sortedRoot, source, "description");
        putObject(sortedRoot, source, "release");
        putObject(sortedRoot, source, "commithash");
        putObject(sortedRoot, source, "processWriters","files", "processors", "isTimestamped");
        putObject(sortedRoot, source, "scenario", "mainModel", "attributesModel", "attributesSimulation", "topography");

        return  StateJsonConverter.deserializeToNode(sortedRoot);
    }
}
