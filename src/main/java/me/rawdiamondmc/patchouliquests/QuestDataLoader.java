package me.rawdiamondmc.patchouliquests;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

public class QuestDataLoader implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return BetterPatchouliQuests.of("better_patchouli_quests");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Find all JSON files under the 'custom_data' directory in datapacks
        Map<Identifier, Resource> resources = manager.findResources("patchouli_quests", id -> id.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier fullId = entry.getKey();
            Resource resource = entry.getValue();

            try (InputStream stream = resource.getInputStream();
                 Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                String path = fullId.getPath();
                // if (path.startsWith("patchouli_quests/")) {
                // Extract the path after 'custom_data/' and remove '.json'
                String dataPath = path.substring(17);
                // if (dataPath.endsWith(".json")) {
                // the path must have .json at tail
                dataPath = dataPath.substring(0, dataPath.length() - 5);
                //}

                Identifier dataId = Identifier.of(fullId.getNamespace(), dataPath);
                JsonElement json = JsonParser.parseReader(reader);
                JsonObject obj = json.getAsJsonObject();
                QuestRegistry.registerQuest(dataId, obj);
                //}
            } catch (Exception e) {
                BetterPatchouliQuests.LOGGER.error("Failed to load quest data '{}'", fullId, e);
            }
        }
        BetterPatchouliQuests.LOGGER.info("Loaded {} quest data entries", QuestRegistry.questSize());
    }
}
