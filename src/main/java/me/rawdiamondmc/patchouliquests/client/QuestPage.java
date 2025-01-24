package me.rawdiamondmc.patchouliquests.client;

import me.rawdiamondmc.patchouliquests.QuestLocation;
import me.rawdiamondmc.patchouliquests.network.packet.c2s.C2SQuestSubmissionPayload;
import me.rawdiamondmc.patchouliquests.network.packet.c2s.C2SRequestTextPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestSubmissionPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestTextPayload;
import org.jetbrains.annotations.Contract;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class QuestPage extends BookPage {
    private transient final QuestLocation location = new QuestLocation(this.book.id, this.entry.getId(), this.pageNum);
    public String title;
    private transient BookTextRenderer textRender;

    @Override
    public void onDisplayed(final GuiBookEntry parent, final int left, final int top) {
        super.onDisplayed(parent, left, top);
        if (!ClientQuestStatusManager.isCompleted(this.location)) {
            final ButtonWidget button = ButtonWidget.builder(Text.translatable("better_patchouli_quests.gui.submit"), this::questButtonClicked).position(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35).size(100, 20).build();
            this.addButton(button);
        }
        final S2CQuestTextPayload payload = BetterPatchouliQuestsClient.requestData(15000, new C2SRequestTextPayload(this.location), S2CQuestTextPayload.ID);
        if (payload == null)
            this.textRender = new BookTextRenderer(parent, Text.translatable("better_patchouli_quests.error.network.timeout"), 0, 12);
        else this.textRender = new BookTextRenderer(parent, payload.result(), 0, 12);
    }

    @Contract(mutates = "this, param")
    private void questButtonClicked(final ButtonWidget button) {
        final S2CQuestSubmissionPayload payload = BetterPatchouliQuestsClient.requestData(15000, new C2SQuestSubmissionPayload(this.location), S2CQuestSubmissionPayload.ID);
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

        this.parent.drawCenteredStringNoShadow(graphics, this.title == null || this.title.isEmpty() ? I18n.translate("patchouli.gui.lexicon.objective") : this.i18n(this.title), GuiBook.PAGE_WIDTH / 2, 0, this.book.headerColor);
        GuiBook.drawSeparator(graphics, this.book, 0, 12);
        this.textRender.render(graphics, mouseX, mouseY, pticks);

        if (ClientQuestStatusManager.isCompleted(this.location)) {
            GuiBook.drawSeparator(graphics, this.book, 0, GuiBook.PAGE_HEIGHT - 25);
            this.parent.drawCenteredStringNoShadow(graphics, I18n.translate("patchouli.gui.lexicon.complete"), GuiBook.PAGE_WIDTH / 2, GuiBook.PAGE_HEIGHT - 17, 0x008b1a);
        }
    }
}
