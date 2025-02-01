package me.rawdiamondmc.patchouliquests.client;

import me.rawdiamondmc.patchouliquests.network.packet.c2s.C2SQuestSubmissionPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestSubmissionPayload;
import org.jetbrains.annotations.Contract;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class QuestPage extends PageWithText {
    String title;
    String quest;
    transient Identifier questId;

    @Override
    public void onDisplayed(final GuiBookEntry parent, final int left, final int top) {
        super.onDisplayed(parent, left, top);
        this.questId = Identifier.of(this.quest);
        if (!ClientQuestStatusManager.isCompleted(questId)) {
            final ButtonWidget button = ButtonWidget.builder(Text.translatable("better_patchouli_quests.gui.submit"), this::questButtonClicked).position(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35).size(100, 20).build();
            this.addButton(button);
        }
    }

    @Override
    public int getTextHeight() {
        return 22;
    }

    @Contract(mutates = "this, param")
    private void questButtonClicked(final ButtonWidget button) {
        final S2CQuestSubmissionPayload payload = BetterPatchouliQuestsClient.requestData(15000, new C2SQuestSubmissionPayload(this.questId), S2CQuestSubmissionPayload.ID);
        if (payload == null)
            button.setMessage(Text.translatable("better_patchouli_quests.error.network.timeout"));
        else if (!payload.isSuccessful()) button.setMessage(payload.result());
        else button.setMessage(Text.translatable("better_patchouli_quests.gui.completed"));
        button.active = false;
        this.entry.markReadStateDirty();
    }

    @Override
    public void render(final DrawContext graphics, final int mouseX, final int mouseY, final float pticks) {
        super.render(graphics, mouseX, mouseY, pticks);

        this.parent.drawCenteredStringNoShadow(graphics, title == null || title.isEmpty() ? I18n.translate("patchouli.gui.lexicon.objective") : i18n(this.title), GuiBook.PAGE_WIDTH / 2, 0, this.book.headerColor);
        GuiBook.drawSeparator(graphics, this.book, 0, 12);

        if (ClientQuestStatusManager.isCompleted(this.questId)) {
            GuiBook.drawSeparator(graphics, this.book, 0, GuiBook.PAGE_HEIGHT - 25);
            this.parent.drawCenteredStringNoShadow(graphics, I18n.translate("patchouli.gui.lexicon.complete"), GuiBook.PAGE_WIDTH / 2, GuiBook.PAGE_HEIGHT - 17, 0x008b1a);
        }
    }
}
