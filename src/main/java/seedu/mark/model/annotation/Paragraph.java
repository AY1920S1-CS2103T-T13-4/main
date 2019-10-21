package seedu.mark.model.annotation;

/**
 * An interface for Paragraphs of offline documents.
 * Paragraphs are to support annotations: highlights and notes.
 */
public abstract class Paragraph {

    public abstract ParagraphIdentifier getId();

    public abstract ParagraphContent getParagraphContent();

    public abstract boolean hasAnnotation();
    public abstract Highlight getHighlight();

    public abstract boolean hasNote();
    public abstract AnnotationNote getNote();

    public abstract void addAnnotation(Annotation annotation);

}
