package seedu.mark.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_HIGHLIGHT;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.mark.logic.parser.CliSyntax.PREFIX_PARAGRAPH;

import java.util.List;

import seedu.mark.commons.core.Messages;
import seedu.mark.commons.core.index.Index;
import seedu.mark.commons.exceptions.IllegalValueException;
import seedu.mark.logic.commands.exceptions.CommandException;
import seedu.mark.logic.commands.results.CommandResult;
import seedu.mark.logic.commands.results.OfflineCommandResult;
import seedu.mark.model.Model;
import seedu.mark.model.annotation.Annotation;
import seedu.mark.model.annotation.AnnotationNote;
import seedu.mark.model.annotation.Highlight;
import seedu.mark.model.annotation.OfflineDocument;
import seedu.mark.model.annotation.ParagraphIdentifier;
import seedu.mark.model.bookmark.Bookmark;
import seedu.mark.model.bookmark.CachedCopy;
import seedu.mark.storage.Storage;

/**
 * Annotates a paragraph with an {@code Annotation}.
 */
public class AddAnnotationCommand extends Command {

    public static final String COMMAND_WORD = "annotate";

    public static final String MESSAGE_CONSTRAINTS =
            "Paragraph identifiers should coincide with the paragraph labels on the left of each paragraph.";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Annotates the cache of the bookmark identified by the index used in the displayed bookmark list.\n"
            + "Parameters: INDEX (must be a positive integer)"
            + PREFIX_PARAGRAPH + "P_ID"
            + "[" + PREFIX_NOTE + "NOTE]"
            + "[" + PREFIX_HIGHLIGHT + "HIGHLIGHT=yellow]"
            + "\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PARAGRAPH + "p3 "
            + PREFIX_NOTE + "a note tagged to paragraph 3 highlighted orange "
            + PREFIX_HIGHLIGHT + "orange";

    public static final String MESSAGE_NO_CACHE_AVAILABLE = "No cache is available to annotate.\n"
            //+ "A cache has been created for you. Kindly press Enter to confirm to add the annotation to this cache."
            + "Download a cache using the " + CacheCommand.COMMAND_WORD + " command to start annotating.";
    public static final String MESSAGE_CANNOT_ANNOTATE_PHANTOM = "You cannot annotate phantom paragraphs.";
    //TODO: change msg to more informative one (what content, to which paragraph, which colour, which bkmark version
    public static final String MESSAGE_SUCCESS = "Annotation successfully added to paragraph %1$s:\n%2$s";
    private static final String MESSAGE_HIGHLIGHT_ADDED = "%s highlight";
    private static final String MESSAGE_NOTE_ADDED = " with note \"%s\"";

    private final Index index;
    private final ParagraphIdentifier pid;
    private final AnnotationNote note;
    private final Highlight highlight;

    public AddAnnotationCommand(Index index, ParagraphIdentifier pid, AnnotationNote note, Highlight highlight) {
        requireNonNull(index);
        requireNonNull(pid);
        requireNonNull(highlight);
        //note can be null which is handled in command result.

        this.index = index;
        this.pid = pid;
        this.note = note;
        this.highlight = highlight;
    }

    @Override
    public CommandResult execute(Model model, Storage storage) throws CommandException {
        requireNonNull(model);
        List<Bookmark> lastShownList = model.getFilteredBookmarkList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_BOOKMARK_DISPLAYED_INDEX);
        }

        Bookmark bookmarkOwnerOfCache = lastShownList.get(index.getZeroBased());
        List<CachedCopy> caches = bookmarkOwnerOfCache.getCachedCopies();
        if (caches.isEmpty()) {
            //TODO: maybe - create cache if it dne so they can annotate immediately. (then need to change ug and dg too)
            throw new CommandException(MESSAGE_NO_CACHE_AVAILABLE);
        }

        if (pid.isStray()) {
            throw new CommandException(MESSAGE_CANNOT_ANNOTATE_PHANTOM);
        }

        String returnMsg;
        //TODO: pick out the appropriate cache. (change ug; default version on newest cache; but can access others too)
        CachedCopy c = caches.get(0);
        OfflineDocument doc = c.getAnnotations();
        Annotation an;
        returnMsg = String.format(MESSAGE_HIGHLIGHT_ADDED, highlight.toString());
        if (note == null) {
            an = new Annotation(highlight);
        } else {
            an = new Annotation(highlight, note);
            returnMsg = returnMsg + String.format(MESSAGE_NOTE_ADDED, note.toString());
        }

        try {
            doc.addAnnotation(pid, an);
        } catch (IllegalValueException e) {
            throw new CommandException(e.getMessage());
        }

        model.updateDocument(doc);

        model.saveMark(String.format(MESSAGE_SUCCESS, pid, returnMsg));
        return new OfflineCommandResult(String.format(MESSAGE_SUCCESS, pid, returnMsg));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddAnnotationCommand // instanceof handles nulls
                && index.equals(((AddAnnotationCommand) other).index)
                && pid.equals(((AddAnnotationCommand) other).pid)
                && (this.note == ((AddAnnotationCommand) other).note
                || note.equals(((AddAnnotationCommand) other).note))
                && highlight.equals(((AddAnnotationCommand) other).highlight)); // state check
    }

}
