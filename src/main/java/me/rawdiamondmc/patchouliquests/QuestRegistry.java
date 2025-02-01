package me.rawdiamondmc.patchouliquests;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import me.rawdiamondmc.patchouliquests.award.QuestAward;
import me.rawdiamondmc.patchouliquests.condition.QuestCondition;
import org.jetbrains.annotations.Contract;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.common.util.SerializationUtil;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public final class QuestRegistry {
    private static final Map<Identifier, Pair<QuestCondition, QuestAward>> quests = new HashMap<>();
    private static final Map<Identifier, Class<? extends QuestCondition>> conditions = new HashMap<>();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(QuestCondition.class, new QuestConditionAdapter())
            .registerTypeAdapter(QuestCondition[].class, new QuestConditionArrayAdapter())
            .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
            .registerTypeAdapter(IVariable.class, SerializationUtil.VARIABLE_SERIALIZER)
            .create();

    private QuestRegistry() {
    }

    public static void registerCondition(final Identifier conditionId, final Class<? extends QuestCondition> condition) {
        if (conditionRegistered(conditionId)) {
            throw new UnsupportedOperationException("'%s' has already been registered!".formatted(conditionId));
        }
        conditions.put(conditionId, condition);
    }

    public static void registerQuest(final Identifier questId, final JsonObject jsonObject) {
        final QuestCondition condition = jsonObject.has("condition") ? gson.fromJson(jsonObject.getAsJsonObject("condition"), QuestCondition.class) : QuestCondition.EMPTY;
        final QuestAward award = jsonObject.has("condition") ? gson.fromJson(jsonObject.getAsJsonObject("award"), QuestAward.class) : QuestAward.EMPTY;
        quests.put(questId, new ObjectObjectImmutablePair<>(condition, award));
    }

    public static void clearQuests() {
        quests.clear();
    }

    public static int questSize() {
        return quests.size();
    }

    @Contract(pure = true)
    public static boolean conditionRegistered(final Identifier questId) {
        return conditions.containsKey(questId);
    }

    @Contract(pure = true)
    public static boolean questRegistered(final Identifier questId) {
        return quests.containsKey(questId);
    }

    public static Optional<Text> check(final Identifier questId, final ServerPlayerEntity player) {
        if (!questRegistered(questId)) {
            return Optional.of(Text.translatable("better_patchouli_quests.error.quest_not_registered"));
        }
        return quests.get(questId).left().check(player);
    }

    public static void award(final Identifier questId, final ServerPlayerEntity player) {
        if (!questRegistered(questId)) return;
        quests.get(questId).right().award(player);
    }

    private static final class QuestConditionAdapter implements JsonDeserializer<QuestCondition> {
        @Override
        public QuestCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException("");
            final JsonObject obj = json.getAsJsonObject();
            String typeString = JsonHelper.getString(obj, "type");
            if (typeString.indexOf(':') < 0) {
                typeString = BetterPatchouliQuests.MOD_ID + ":" + typeString;
            }
            final Identifier type = Identifier.of(typeString);
            final Class<? extends QuestCondition> clazz = QuestRegistry.conditions.get(type);
            return SerializationUtil.RAW_GSON.fromJson(json, clazz);
        }
    }

    private static final class QuestConditionArrayAdapter implements JsonDeserializer<QuestCondition[]> {
        @Override
        public QuestCondition[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonArray()) throw new JsonParseException("");
            final JsonArray array = json.getAsJsonArray();
            return array.asList().stream().map(jsonElement -> gson.fromJson(jsonElement, QuestCondition.class)).toArray(QuestCondition[]::new);
        }
    }
}
